package com.smarttender.notification.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity @Table(name = "notification_log", schema = "smart_tender_notifications")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class NotificationLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "user_id", nullable = false) private Long userId;
    @Column(name = "tender_id", length = 200) private String tenderId;
    @Column(name = "channel", length = 20) private String channel;
    @Column(name = "recipient", length = 200) private String recipient;
    @Column(name = "subject", length = 500) private String subject;
    @Column(name = "status", length = 20) private String status;
    @Column(name = "error_message", columnDefinition = "TEXT") private String errorMessage;
    @Column(name = "retry_count") @Builder.Default private int retryCount = 0;
    @Column(name = "sent_at") private LocalDateTime sentAt;
    @Column(name = "created_at") private LocalDateTime createdAt;
    @PrePersist public void prePersist() { this.createdAt = LocalDateTime.now(); }
}
