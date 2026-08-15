package com.smarttender.search.service.impl;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.smarttender.common.dto.PagedResponse;
import com.smarttender.search.document.TenderSearchDocument;
import com.smarttender.search.dto.request.TenderSearchRequest;
import com.smarttender.search.dto.response.TenderSearchResult;
import com.smarttender.search.query.TenderQueryBuilder;
import com.smarttender.search.service.TenderSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j @Service @RequiredArgsConstructor
public class TenderSearchServiceImpl implements TenderSearchService {
    private final ElasticsearchClient esClient;
    private final TenderQueryBuilder queryBuilder;

    @Override
    public PagedResponse<TenderSearchResult> search(TenderSearchRequest req) {
        try {
            SearchResponse<TenderSearchDocument> response = esClient.search(
                    queryBuilder.buildSearchRequest(req), TenderSearchDocument.class);
            List<TenderSearchResult> results = response.hits().hits().stream()
                    .map(Hit::source).filter(s -> s != null).map(this::toResult)
                    .collect(Collectors.toList());
            long total = response.hits().total() != null ? response.hits().total().value() : 0;
            int totalPages = req.getSize() > 0 ? (int) Math.ceil((double) total / req.getSize()) : 0;
            return PagedResponse.<TenderSearchResult>builder()
                    .content(results).page(req.getPage()).size(req.getSize())
                    .totalElements(total).totalPages(totalPages)
                    .first(req.getPage() == 0).last(req.getPage() >= totalPages - 1).build();
        } catch (IOException e) {
            log.error("Elasticsearch search failed: {}", e.getMessage(), e);
            throw new RuntimeException("Search failed", e);
        }
    }

    @Override
    public TenderSearchResult getById(String tenderId) {
        try {
            var resp = esClient.get(g -> g.index("tenders").id(tenderId), TenderSearchDocument.class);
            if (resp.found() && resp.source() != null) return toResult(resp.source());
            throw new com.smarttender.common.exception.ResourceNotFoundException("Tender", "id", tenderId);
        } catch (IOException e) {
            throw new RuntimeException("Get tender failed", e);
        }
    }

    private TenderSearchResult toResult(TenderSearchDocument doc) {
        return TenderSearchResult.builder()
                .tenderId(doc.getTenderId()).title(doc.getTitle()).tenderRefNo(doc.getTenderRefNo())
                .organisationName(doc.getOrganisationName()).productCategory(doc.getProductCategory())
                .sourceType(doc.getSourceType()).tenderStatus(doc.getTenderStatus())
                .publishedDate(doc.getPublishedDate()).bidSubmissionClosingDate(doc.getBidSubmissionClosingDate())
                .daysUntilClosing(doc.getDaysUntilClosing()).relevanceScore(doc.getRelevanceScore())
                .detailUrl(doc.getDetailUrl()).extractedKeywords(doc.getExtractedKeywords()).build();
    }
}
