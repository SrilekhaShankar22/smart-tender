package com.smarttender.common.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

/** Kafka event published to topic tender.alerts when a tender matches user profiles */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class NotificationAlertEvent {
    private String tenderId;
    private String tenderTitle;
    private String organisationName;
    private String tenderRefNo;
    private String detailUrl;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime bidSubmissionClosingDate;
    private long daysUntilClosing;
    private double relevanceScore;
    private List<Long> matchedUserIds;
    private String matchedProfileName;
    private List<String> matchedKeywords;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime alertCreatedAt;
}
