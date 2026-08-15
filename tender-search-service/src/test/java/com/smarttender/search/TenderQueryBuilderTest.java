package com.smarttender.search;

import com.smarttender.search.dto.request.TenderSearchRequest;
import com.smarttender.search.query.TenderQueryBuilder;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class TenderQueryBuilderTest {

    private final TenderQueryBuilder builder = new TenderQueryBuilder();

    @Test
    void builds_search_request_with_keyword() {
        TenderSearchRequest req = TenderSearchRequest.builder()
                .keyword("civil construction")
                .page(0).size(20).build();

        SearchRequest request = builder.buildSearchRequest(req);

        assertThat(request).isNotNull();
        assertThat(request.index()).containsExactly("tenders");
        assertThat(request.from()).isEqualTo(0);
        assertThat(request.size()).isEqualTo(20);
    }

    @Test
    void builds_search_request_with_filters() {
        TenderSearchRequest req = TenderSearchRequest.builder()
                .organisation("CPWD").sourceType("CENTRAL")
                .tenderStatus("ACTIVE").page(0).size(10).build();

        SearchRequest request = builder.buildSearchRequest(req);

        assertThat(request).isNotNull();
        assertThat(request.size()).isEqualTo(10);
    }

    @Test
    void builds_match_all_when_no_filters() {
        TenderSearchRequest req = TenderSearchRequest.builder().page(0).size(20).build();
        SearchRequest request = builder.buildSearchRequest(req);
        assertThat(request).isNotNull();
    }
}
