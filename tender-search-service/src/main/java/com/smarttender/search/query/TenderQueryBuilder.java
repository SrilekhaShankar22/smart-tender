package com.smarttender.search.query;
import com.smarttender.search.dto.request.TenderSearchRequest;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * TenderQueryBuilder — Builder Pattern for Elasticsearch query construction.
 * Constructs complex bool queries from TenderSearchRequest parameters.
 */
@Slf4j @Component
public class TenderQueryBuilder {

    public SearchRequest buildSearchRequest(TenderSearchRequest req) {
        List<Query> mustQueries  = new ArrayList<>();
        List<Query> filterQueries = new ArrayList<>();

        // Full-text search on title and description
        if (req.getKeyword() != null && !req.getKeyword().isBlank()) {
            mustQueries.add(Query.of(q -> q.multiMatch(m -> m
                    .query(req.getKeyword())
                    .fields("title^3", "organisationName^2", "extractedKeywords^2", "fullDescription")
                    .type(TextQueryType.BestFields)
                    .fuzziness("AUTO"))));
        }

        // Filters (exact match — don't affect score)
        if (req.getOrganisation() != null && !req.getOrganisation().isBlank()) {
            filterQueries.add(Query.of(q -> q.match(m -> m.field("organisationName").query(req.getOrganisation()))));
        }
        if (req.getSourceType() != null && !req.getSourceType().isBlank()) {
            filterQueries.add(Query.of(q -> q.term(t -> t.field("sourceType").value(req.getSourceType()))));
        }
        if (req.getTenderStatus() != null && !req.getTenderStatus().isBlank()) {
            filterQueries.add(Query.of(q -> q.term(t -> t.field("tenderStatus").value(req.getTenderStatus()))));
        }
        if (req.getCategory() != null && !req.getCategory().isBlank()) {
            filterQueries.add(Query.of(q -> q.match(m -> m.field("productCategory").query(req.getCategory()))));
        }

        // Date range filters
        addDateRangeFilter(filterQueries, "bidSubmissionClosingDate", req.getClosingDateFrom(), req.getClosingDateTo());
        addDateRangeFilter(filterQueries, "publishedDate", req.getPublishedDateFrom(), req.getPublishedDateTo());

        // Build final bool query
        Query finalQuery = mustQueries.isEmpty() && filterQueries.isEmpty()
                ? Query.of(q -> q.matchAll(m -> m))
                : Query.of(q -> q.bool(b -> {
                    mustQueries.forEach(mq -> b.must(mq));
                    filterQueries.forEach(fq -> b.filter(fq));
                    return b;
                  }));

        // Determine sort
        String sortField = req.getSortBy() != null ? req.getSortBy() : "relevanceScore";
        boolean isDesc   = !"asc".equalsIgnoreCase(req.getSortDirection());

        final Query fQuery = finalQuery;
        return SearchRequest.of(s -> s
                .index("tenders")
                .from(req.getPage() * req.getSize())
                .size(req.getSize())
                .query(fQuery)
                .sort(so -> so.field(f -> f.field(sortField).order(isDesc ? SortOrder.Desc : SortOrder.Asc)))
        );
    }

    private void addDateRangeFilter(List<Query> filters, String field, String from, String to) {
        if ((from == null || from.isBlank()) && (to == null || to.isBlank())) return;
        filters.add(Query.of(q -> q.range(r -> {
            r.field(field);
            if (from != null && !from.isBlank()) r.gte(co.elastic.clients.json.JsonData.of(from));
            if (to   != null && !to.isBlank())   r.lte(co.elastic.clients.json.JsonData.of(to));
            return r;
        })));
    }
}
