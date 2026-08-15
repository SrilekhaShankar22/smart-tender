package com.smarttender.profile.entity;
import jakarta.persistence.*;
import lombok.*;
@Entity @Table(name = "notification_preferences", schema = "smart_tender_profiles")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class NotificationPreference {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "user_id", nullable = false, unique = true) private Long userId;
    @Column(name = "email_enabled") @Builder.Default private boolean emailEnabled = true;
    @Column(name = "push_enabled") @Builder.Default private boolean pushEnabled = false;
    @Column(name = "notification_frequency", length = 20) @Builder.Default private String notificationFrequency = "DAILY";
    @Column(name = "min_relevance_score") @Builder.Default private double minRelevanceScore = 0.5;
    @Column(name = "notify_closing_soon") @Builder.Default private boolean notifyClosingSoon = true;
    @Column(name = "closing_soon_days") @Builder.Default private int closingSoonDays = 3;
}
