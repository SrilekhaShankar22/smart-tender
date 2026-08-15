package com.smarttender.fetch.service;
import com.smarttender.common.event.TenderRawEvent;
import com.smarttender.common.enums.SourceType;
import com.smarttender.common.util.HashUtil;
import com.smarttender.fetch.config.AppProperties;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * GemPortalScraper — scrapes eprocure.gov.in tender listings.
 *
 * Confirmed live portal details:
 *  URL: https://eprocure.gov.in/cppp/latestactivetendersnew/cpppdata?page=N
 *  Date format: dd-MMM-yyyy hh:mm a  (e.g. "04-May-2026 02:21 PM")
 *  No CAPTCHA on listing pages.
 *  7 columns: Sl.No | Published | BidClosing | Opening | Title+Ref | Org | Corrigendum
 */
@Slf4j @Service @RequiredArgsConstructor
public class GemPortalScraper {

    private final WebClient gemWebClient;
    private final AppProperties props;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a");
    public static final String PORTAL_BASE       = "https://eprocure.gov.in";
    public static final String ENDPOINT_CENTRAL  = "/cppp/latestactivetendersnew/cpppdata";
    public static final String ENDPOINT_STATE    = "/cppp/latestactivetendersnew/mmpdata";
    public static final String ENDPOINT_GEM      = "/cppp/latestactivetendersnew/gemdata";

    @CircuitBreaker(name = "gemPortal", fallbackMethod = "fetchPageFallback")
    @Retry(name = "gemPortal")
    public List<TenderRawEvent> fetchPage(String endpoint, int page, SourceType sourceType, String jobId) {
        String url = PORTAL_BASE + endpoint + "?page=" + page;
        log.info("[{}] Fetching page {} → {}", sourceType, page, url);
        try {
            String html = gemWebClient.get().uri(url).retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(30)).block();
            if (html == null || html.isBlank()) { log.warn("Empty response"); return Collections.emptyList(); }
            sleep(props.getGem().getRequestDelayMs());
            return parseTenderTable(Jsoup.parse(html), page, sourceType, jobId);
        } catch (Exception e) {
            log.error("Fetch failed page={} source={}: {}", page, sourceType, e.getMessage());
            throw e;
        }
    }

    public int getTotalCount(String endpoint) {
        try {
            String html = gemWebClient.get().uri(PORTAL_BASE + endpoint)
                    .retrieve().bodyToMono(String.class).timeout(Duration.ofSeconds(30)).block();
            if (html == null) return -1;
            Element el = Jsoup.parse(html).selectFirst(
                    "div:containsOwn(Total Tenders),span:containsOwn(Total Tenders)");
            if (el == null) return -1;
            return Integer.parseInt(el.text().replaceAll("[^0-9]", ""));
        } catch (Exception e) { return -1; }
    }

    private List<TenderRawEvent> parseTenderTable(Document doc, int page, SourceType st, String jobId) {
        List<TenderRawEvent> list = new ArrayList<>();
        Elements rows = doc.select("table tbody tr");
        if (rows.isEmpty()) { log.warn("No tbody rows found — portal structure may have changed"); return list; }
        for (Element row : rows) {
            try {
                TenderRawEvent e = parseRow(row, page, st, jobId);
                if (e != null) list.add(e);
            } catch (Exception ex) {
                log.warn("Skipping row: {}", ex.getMessage());
            }
        }
        log.info("[{}] Parsed {} tenders from page {}", st, list.size(), page);
        return list;
    }

    private TenderRawEvent parseRow(Element row, int page, SourceType st, String jobId) {
        Elements cells = row.select("td");
        if (cells.size() < 6) return null;
        String publishedStr  = cells.get(1).text().trim();
        String bidCloseStr   = cells.get(2).text().trim();
        String openingStr    = cells.get(3).text().trim();
        Element titleCell    = cells.get(4);
        Element anchor       = titleCell.selectFirst("a");
        String title         = anchor != null ? anchor.text().trim() : titleCell.text().trim();
        String detailUrl     = null;
        if (anchor != null) {
            String href = anchor.attr("href").trim();
            if (!href.isBlank()) detailUrl = href.startsWith("http") ? href : PORTAL_BASE + href;
        }
        String fullText = titleCell.text().trim();
        String refAndId = fullText.startsWith(title) ? fullText.substring(title.length()).trim() : fullText;
        String tenderId  = extractTenderId(refAndId, detailUrl);
        String refNo     = extractRefNo(refAndId);
        String org       = cells.get(5).text().trim();
        String corr      = cells.size() > 6 ? cells.get(6).text().trim() : null;
        if ("--".equals(corr)) corr = null;
        String hash = HashUtil.hashSha256(HashUtil.buildTenderHashInput(tenderId, title, org, publishedStr));
        return TenderRawEvent.builder()
                .tenderId(tenderId).title(title).tenderRefNo(refNo)
                .organisationName(org).corrigendum(corr).sourceType(st)
                .publishedDate(parseDate(publishedStr))
                .bidSubmissionClosingDate(parseDate(bidCloseStr))
                .tenderOpeningDate(parseDate(openingStr))
                .contentHash(hash).detailUrl(detailUrl)
                .sourceUrl(PORTAL_BASE + endpoint(st))
                .pageNumber(page).fetchJobId(jobId)
                .fetchedAt(LocalDateTime.now()).build();
    }

    private String extractTenderId(String ref, String detailUrl) {
        if (ref != null && !ref.isBlank()) {
            for (String p : ref.split("[/\\s]+"))
                if (p.trim().matches("\\d{4,}")) return p.trim();
            String c = ref.replaceAll("^[/\\s]+", "").trim();
            if (!c.isBlank()) return c;
        }
        if (detailUrl != null) {
            String[] parts = detailUrl.split("/");
            if (parts.length > 0) return parts[parts.length - 1].substring(0, Math.min(80, parts[parts.length-1].length()));
        }
        return "UNKNOWN_" + System.currentTimeMillis();
    }

    private String extractRefNo(String ref) {
        if (ref == null || ref.isBlank()) return null;
        String c = ref.replaceAll("^[/\\s]+", "").trim();
        int ls = c.lastIndexOf('/');
        if (ls > 0 && c.substring(ls + 1).matches("\\d+")) return c.substring(0, ls).trim();
        return c;
    }

    private String endpoint(SourceType t) {
        return switch (t) { case STATE -> ENDPOINT_STATE; case GEM -> ENDPOINT_GEM; default -> ENDPOINT_CENTRAL; };
    }

    private LocalDateTime parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        try { return LocalDateTime.parse(s, DATE_FMT); }
        catch (DateTimeParseException e) { return null; }
    }

    public List<TenderRawEvent> fetchPageFallback(String ep, int pg, SourceType st, String jobId, Throwable t) {
        log.error("Circuit OPEN [{} page {}]: {}", st, pg, t.getMessage());
        return Collections.emptyList();
    }

    private void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
