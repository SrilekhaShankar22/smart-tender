package com.smarttender.profile.entity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;
import java.util.List;
@Entity @Table(name = "saved_search", schema = "smart_tender_profiles")
@EntityListeners(AuditingEntityListener.class)
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SavedSearch {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "user_id", nullable = false) private Long userId;
    @Column(name = "name", nullable = false, length = 100) private String name;
    @Column(name = "keywords", length = 500) private String keywords;
    @Column(name = "organisation", length = 300) private String organisation;
    @Column(name = "category", length = 200) private String category;
    @Column(name = "source_type", length = 20) private String sourceType;
    @Column(name = "state", length = 100) private String state;
    @Column(name = "alert_enabled") @Builder.Default private boolean alertEnabled = true;
    @Column(name = "alert_frequency", length = 20) @Builder.Default private String alertFrequency = "DAILY";
    @CreatedDate @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;
    @PreUpdate public void preUpdate() { this.updatedAt = LocalDateTime.now(); }
}
