package com.smarttender.fetch.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fetched_tender", schema = "smart_tender_fetch",
    indexes = {
        @Index(name = "idx_ft_tender_id", columnList = "tender_id"),
        @Index(name = "idx_ft_hash", columnList = "content_hash"),
        @Index(name = "idx_ft_source", columnList = "source_type")
    })
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class FetchedTender {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "tender_id", nullable = false, unique = true, length = 200) private String tenderId;
    @Column(name = "content_hash", nullable = false, length = 64) private String contentHash;
    @Column(name = "tender_ref_no", length = 300) private String tenderRefNo;
    @Column(name = "title", length = 500) private String title;
    @Column(name = "organisation_name", length = 300) private String organisationName;
    @Column(name = "detail_url", length = 1000) private String detailUrl;
    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false, length = 20) private SourceType sourceType;
    @Column(name = "published_date") private LocalDateTime publishedDate;
    @Column(name = "closing_date") private LocalDateTime closingDate;
    @Column(name = "first_seen_at", nullable = false) private LocalDateTime firstSeenAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fetch_log_id") private FetchLog fetchLog;
    public enum SourceType { CENTRAL, STATE, GEM }
}
