package com.smarttender.processing.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity @Table(name = "processed_tender", schema = "smart_tender_processing")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ProcessedTender {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "tender_id", unique = true, nullable = false) private String tenderId;
    @Column(name = "content_hash", length = 64) private String contentHash;
    @Column(name = "title", length = 500) private String title;
    @Column(name = "organisation_name", length = 300) private String organisationName;
    @Column(name = "source_type", length = 20) private String sourceType;
    @Column(name = "tender_status", length = 20) private String tenderStatus;
    @Column(name = "relevance_score") private double relevanceScore;
    @Column(name = "is_duplicate") private boolean isDuplicate;
    @Column(name = "closing_date") private LocalDateTime closingDate;
    @Column(name = "processed_at") private LocalDateTime processedAt;
    @Column(name = "es_indexed") private boolean esIndexed;
}
