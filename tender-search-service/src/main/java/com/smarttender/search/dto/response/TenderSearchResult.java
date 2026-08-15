package com.smarttender.search.dto.response;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TenderSearchResult {
    private String tenderId;
    private String title;
    private String tenderRefNo;
    private String organisationName;
    private String productCategory;
    private String sourceType;
    private String tenderStatus;
    private LocalDateTime publishedDate;
    private LocalDateTime bidSubmissionClosingDate;
    private long daysUntilClosing;
    private double relevanceScore;
    private String detailUrl;
    private List<String> extractedKeywords;
    private String location;
}
