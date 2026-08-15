package com.smarttender.search.document;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TenderSearchDocument {
    private String tenderId, title, tenderRefNo, organisationName, productCategory;
    private String tenderType, location, sourceType, tenderStatus, detailUrl, contentHash;
    private LocalDateTime publishedDate, bidSubmissionClosingDate, tenderOpeningDate, processedAt;
    private List<String> extractedKeywords;
    private double relevanceScore;
    private long daysUntilClosing;
    private boolean isDuplicate;
}
