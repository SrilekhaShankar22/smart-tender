package com.smarttender.fetch.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "fetch_log", schema = "smart_tender_fetch")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class FetchLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "job_id", nullable = false, length = 100) private String jobId;
    @Column(name = "started_at", nullable = false) private LocalDateTime startedAt;
    @Column(name = "completed_at") private LocalDateTime completedAt;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20) private FetchStatus status;
    @Column(name = "pages_fetched") @Builder.Default private int pagesFetched = 0;
    @Column(name = "tenders_found") @Builder.Default private int tendersFound = 0;
    @Column(name = "new_tenders") @Builder.Default private int newTenders = 0;
    @Column(name = "error_message", columnDefinition = "TEXT") private String errorMessage;
    @Column(name = "source_type", length = 20) private String sourceType;
    @Column(name = "created_at") private LocalDateTime createdAt;
    @PrePersist public void prePersist() { this.createdAt = LocalDateTime.now(); }
    public enum FetchStatus { RUNNING, SUCCESS, FAILED }
}
