package com.smarttender.profile.service;
import com.smarttender.profile.dto.request.SavedSearchRequest;
import com.smarttender.profile.dto.response.SavedSearchResponse;
import java.util.List;
public interface SavedSearchService {
    SavedSearchResponse create(Long userId, SavedSearchRequest request);
    List<SavedSearchResponse> getByUser(Long userId);
    SavedSearchResponse update(Long id, Long userId, SavedSearchRequest request);
    void delete(Long id, Long userId);
}
