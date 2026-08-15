package com.smarttender.processing.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity @Table(name = "processing_log", schema = "smart_tender_processing")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ProcessingLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "tender_id") private String tenderId;
    @Column(name = "status", length = 20) private String status;
    @Column(name = "error_message", columnDefinition = "TEXT") private String errorMessage;
    @Column(name = "processing_time_ms") private long processingTimeMs;
    @Column(name = "created_at") private LocalDateTime createdAt;
    @PrePersist public void prePersist() { this.createdAt = LocalDateTime.now(); }
}
