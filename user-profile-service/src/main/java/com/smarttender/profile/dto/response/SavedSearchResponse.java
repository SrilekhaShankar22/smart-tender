package com.smarttender.profile.dto.response;
import lombok.*;
import java.time.LocalDateTime;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SavedSearchResponse {
    private Long id;
    private String name, keywords, organisation, category, sourceType, state;
    private boolean alertEnabled;
    private String alertFrequency;
    private LocalDateTime createdAt;
}
