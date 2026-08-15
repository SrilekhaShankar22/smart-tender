package com.smarttender.profile.service.impl;
import com.smarttender.common.exception.ResourceNotFoundException;
import com.smarttender.common.exception.SmartTenderException;
import com.smarttender.profile.dto.request.SavedSearchRequest;
import com.smarttender.profile.dto.response.SavedSearchResponse;
import com.smarttender.profile.entity.SavedSearch;
import com.smarttender.profile.repository.SavedSearchRepository;
import com.smarttender.profile.service.SavedSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
@Service @RequiredArgsConstructor @Transactional
public class SavedSearchServiceImpl implements SavedSearchService {
    private final SavedSearchRepository repo;
    @Override public SavedSearchResponse create(Long userId, SavedSearchRequest req) {
        SavedSearch s = SavedSearch.builder().userId(userId).name(req.getName())
                .keywords(req.getKeywords()).organisation(req.getOrganisation())
                .category(req.getCategory()).sourceType(req.getSourceType())
                .state(req.getState()).alertEnabled(req.isAlertEnabled())
                .alertFrequency(req.getAlertFrequency()).build();
        return toResponse(repo.save(s));
    }
    @Override public List<SavedSearchResponse> getByUser(Long userId) {
        return repo.findByUserId(userId).stream().map(this::toResponse).collect(Collectors.toList());
    }
    @Override public SavedSearchResponse update(Long id, Long userId, SavedSearchRequest req) {
        SavedSearch s = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("SavedSearch","id",id));
        if (!s.getUserId().equals(userId)) throw new SmartTenderException("FORBIDDEN","Not your saved search");
        s.setName(req.getName()); s.setKeywords(req.getKeywords()); s.setOrganisation(req.getOrganisation());
        s.setCategory(req.getCategory()); s.setSourceType(req.getSourceType()); s.setState(req.getState());
        s.setAlertEnabled(req.isAlertEnabled()); s.setAlertFrequency(req.getAlertFrequency());
        return toResponse(repo.save(s));
    }
    @Override public void delete(Long id, Long userId) {
        SavedSearch s = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("SavedSearch","id",id));
        if (!s.getUserId().equals(userId)) throw new SmartTenderException("FORBIDDEN","Not your saved search");
        repo.delete(s);
    }
    private SavedSearchResponse toResponse(SavedSearch s) {
        return SavedSearchResponse.builder().id(s.getId()).name(s.getName()).keywords(s.getKeywords())
                .organisation(s.getOrganisation()).category(s.getCategory()).sourceType(s.getSourceType())
                .state(s.getState()).alertEnabled(s.isAlertEnabled()).alertFrequency(s.getAlertFrequency())
                .createdAt(s.getCreatedAt()).build();
    }
}
