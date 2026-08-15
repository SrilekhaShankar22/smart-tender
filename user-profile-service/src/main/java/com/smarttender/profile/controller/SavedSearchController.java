package com.smarttender.profile.controller;
import com.smarttender.common.dto.ApiResponse;
import com.smarttender.profile.dto.request.SavedSearchRequest;
import com.smarttender.profile.dto.response.SavedSearchResponse;
import com.smarttender.profile.service.SavedSearchService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/v1/profiles/{userId}/saved-searches")
@RequiredArgsConstructor @Tag(name = "Saved Searches")
public class SavedSearchController {
    private final SavedSearchService service;
    @PostMapping
    public ResponseEntity<ApiResponse<SavedSearchResponse>> create(
            @PathVariable Long userId, @Valid @RequestBody SavedSearchRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Created", service.create(userId, req)));
    }
    @GetMapping
    public ResponseEntity<ApiResponse<List<SavedSearchResponse>>> list(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(service.getByUser(userId)));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SavedSearchResponse>> update(
            @PathVariable Long userId, @PathVariable Long id, @Valid @RequestBody SavedSearchRequest req) {
        return ResponseEntity.ok(ApiResponse.success("Updated", service.update(id, userId, req)));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long userId, @PathVariable Long id) {
        service.delete(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Deleted", null));
    }
}
