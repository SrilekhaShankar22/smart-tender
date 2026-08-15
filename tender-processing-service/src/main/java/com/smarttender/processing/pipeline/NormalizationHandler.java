package com.smarttender.processing.pipeline;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
@Component @Order(2)
public class NormalizationHandler extends AbstractProcessingHandler {
    @Override public int getOrder() { return 2; }
    @Override
    protected void doHandle(ProcessingContext ctx) {
        var raw = ctx.getRawEvent();
        ctx.getBuilder()
            .tenderId(raw.getTenderId())
            .title(normalize(raw.getTitle()))
            .tenderRefNo(raw.getTenderRefNo())
            .organisationName(normalize(raw.getOrganisationName()))
            .productCategory(raw.getProductCategory())
            .sourceType(raw.getSourceType())
            .corrigendum(raw.getCorrigendum())
            .publishedDate(raw.getPublishedDate())
            .bidSubmissionClosingDate(raw.getBidSubmissionClosingDate())
            .tenderOpeningDate(raw.getTenderOpeningDate())
            .detailUrl(raw.getDetailUrl())
            .contentHash(raw.getContentHash());
    }
    private String normalize(String s) {
        if (s == null) return null;
        return s.trim().replaceAll("\\s+", " ");
    }
}
