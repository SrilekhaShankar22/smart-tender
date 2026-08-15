package com.smarttender.profile.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SavedSearchRequest {
    @NotBlank @Size(max = 100)
    private String name;
    private String keywords;
    private String organisation;
    private String category;
    private String sourceType;
    private String state;
    private boolean alertEnabled = true;
    private String alertFrequency = "DAILY";
}
