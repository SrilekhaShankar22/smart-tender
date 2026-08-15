package com.smarttender.search.dto.request;
import jakarta.validation.constraints.*;
import lombok.*;
@Data @Builder
public class TenderSearchRequest {
    private String keyword;
    private String organisation;
    private String state;
    private String category;
    private String sourceType;    // CENTRAL|STATE|GEM
    private String tenderStatus;  // ACTIVE|CLOSING_SOON|EXPIRED
    private String closingDateFrom;
    private String closingDateTo;
    private String publishedDateFrom;
    private String publishedDateTo;
    private String sortBy;        // relevanceScore|publishedDate|closingDate
    private String sortDirection; // asc|desc
    @Builder.Default @Min(0) private int page = 0;
    @Builder.Default @Min(1) @Max(100) private int size = 20;
}
