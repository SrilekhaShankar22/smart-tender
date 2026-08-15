package com.smarttender.common.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.smarttender.common.enums.SourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/** Kafka event published by tender-fetch-service to topic tender.raw */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TenderRawEvent {
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
    private String sourceUrl;
    private String contentHash;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fetchedAt;
    private int pageNumber;
    private String fetchJobId;
}
