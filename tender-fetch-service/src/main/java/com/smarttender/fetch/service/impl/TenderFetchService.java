package com.smarttender.fetch.service.impl;
import com.smarttender.common.enums.SourceType;
import com.smarttender.common.event.TenderRawEvent;
import com.smarttender.fetch.config.AppProperties;
import com.smarttender.fetch.entity.FetchLog;
import com.smarttender.fetch.entity.FetchedTender;
import com.smarttender.fetch.kafka.TenderRawEventPublisher;
import com.smarttender.fetch.repository.*;
import com.smarttender.fetch.service.GemPortalScraper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j @Service @RequiredArgsConstructor
public class TenderFetchService {
    private final GemPortalScraper scraper;
    private final TenderRawEventPublisher publisher;
    private final FetchLogRepository fetchLogRepo;
    private final FetchedTenderRepository fetchedTenderRepo;
    private final AppProperties props;

    public void runFetchCycle(String jobId) {
        log.info("═══ Fetch cycle START | jobId={} ═══", jobId);
        FetchLog fetchLog = createLog(jobId);
        try {
            int total = 0;
            total += fetchSource(GemPortalScraper.ENDPOINT_CENTRAL, SourceType.CENTRAL, fetchLog);
            total += fetchSource(GemPortalScraper.ENDPOINT_STATE,   SourceType.STATE,   fetchLog);
            total += fetchSource(GemPortalScraper.ENDPOINT_GEM,     SourceType.GEM,     fetchLog);
            markSuccess(fetchLog, props.getGem().getFetchPageSize() * 3, total);
            log.info("═══ Fetch cycle DONE | published={} ═══", total);
        } catch (Exception e) {
            log.error("═══ Fetch cycle FAILED: {} ═══", e.getMessage(), e);
            markFailed(fetchLog, e.getMessage());
        }
    }

    private int fetchSource(String endpoint, SourceType sourceType, FetchLog fetchLog) {
        int pages = props.getGem().getFetchPageSize(), published = 0;
        for (int page = 1; page <= pages; page++) {
            List<TenderRawEvent> events = scraper.fetchPage(endpoint, page, sourceType, fetchLog.getJobId());
            if (events.isEmpty()) { log.info("[{}] Page {} empty — stopping", sourceType, page); break; }
            published += processAndPublish(events, fetchLog);
        }
        return published;
    }

    @Transactional
    public int processAndPublish(List<TenderRawEvent> events, FetchLog fetchLog) {
        int count = 0;
        for (TenderRawEvent event : events) {
            try {
                if (fetchedTenderRepo.existsByTenderId(event.getTenderId()) ||
                    fetchedTenderRepo.existsByContentHash(event.getContentHash())) {
                    log.debug("DUPLICATE skipped: {}", event.getTenderId()); continue;
                }
                fetchedTenderRepo.save(FetchedTender.builder()
                        .tenderId(event.getTenderId()).contentHash(event.getContentHash())
                        .tenderRefNo(event.getTenderRefNo()).title(event.getTitle())
                        .organisationName(event.getOrganisationName()).detailUrl(event.getDetailUrl())
                        .sourceType(FetchedTender.SourceType.valueOf(event.getSourceType().name()))
                        .publishedDate(event.getPublishedDate()).closingDate(event.getBidSubmissionClosingDate())
                        .firstSeenAt(LocalDateTime.now()).fetchLog(fetchLog).build());
                publisher.publish(event);
                count++;
            } catch (Exception e) {
                log.error("Error processing tenderId={}: {}", event.getTenderId(), e.getMessage());
            }
        }
        return count;
    }

    @Transactional
    protected FetchLog createLog(String jobId) {
        return fetchLogRepo.save(FetchLog.builder().jobId(jobId)
                .startedAt(LocalDateTime.now()).status(FetchLog.FetchStatus.RUNNING).build());
    }
    @Transactional
    protected void markSuccess(FetchLog fl, int pages, int tenders) {
        fl.setStatus(FetchLog.FetchStatus.SUCCESS); fl.setCompletedAt(LocalDateTime.now());
        fl.setPagesFetched(pages); fl.setNewTenders(tenders);
        fetchLogRepo.save(fl);
    }
    @Transactional
    protected void markFailed(FetchLog fl, String error) {
        fl.setStatus(FetchLog.FetchStatus.FAILED); fl.setCompletedAt(LocalDateTime.now());
        fl.setErrorMessage(error); fetchLogRepo.save(fl);
    }
}
