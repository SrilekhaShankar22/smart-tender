package com.smarttender.processing.entity;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import java.time.LocalDateTime;
import java.util.List;

@Document(indexName = "tenders")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TenderDocument {
    @Id private String id;
    @Field(type = FieldType.Keyword) private String tenderId;
    @Field(type = FieldType.Text, analyzer = "standard") private String title;
    @Field(type = FieldType.Keyword) private String tenderRefNo;
    @Field(type = FieldType.Text, analyzer = "standard") private String organisationName;
    @Field(type = FieldType.Keyword) private String productCategory;
    @Field(type = FieldType.Keyword) private String tenderType;
    @Field(type = FieldType.Keyword) private String location;
    @Field(type = FieldType.Keyword) private String sourceType;
    @Field(type = FieldType.Keyword) private String tenderStatus;
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second) private LocalDateTime publishedDate;
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second) private LocalDateTime bidSubmissionClosingDate;
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second) private LocalDateTime tenderOpeningDate;
    @Field(type = FieldType.Text, analyzer = "standard") private String fullDescription;
    @Field(type = FieldType.Keyword) private List<String> extractedKeywords;
    @Field(type = FieldType.Double) private double relevanceScore;
    @Field(type = FieldType.Keyword) private String contentHash;
    @Field(type = FieldType.Keyword) private String detailUrl;
    @Field(type = FieldType.Long) private long daysUntilClosing;
    @Field(type = FieldType.Boolean) private boolean isDuplicate;
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second) private LocalDateTime processedAt;
}
