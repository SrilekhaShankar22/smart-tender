package com.smarttender.common.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.smarttender.common.enums.SourceType;
import com.smarttender.common.enums.TenderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

/** Kafka event published by tender-processing-service to topic tender.processed */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TenderProcessedEvent {
    private String tenderId;
    private String title;
    private String tenderRefNo;
    private String organisationName;
    private String productCategory;
    private String tenderType;
    private String location;
    private String corrigendum;
    private SourceType sourceType;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime publishedDate;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime bidSubmissionClosingDate;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime tenderOpeningDate;
    private String detailUrl;
    private String contentHash;
    private String fullDescription;
    private List<String> extractedKeywords;
    private double relevanceScore;
    private boolean isDuplicate;
    private TenderStatus tenderStatus;
    private long daysUntilClosing;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime processedAt;
}
