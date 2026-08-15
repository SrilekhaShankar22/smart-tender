package com.smarttender.search.controller;
import com.smarttender.common.dto.ApiResponse;
import com.smarttender.common.dto.PagedResponse;
import com.smarttender.search.dto.request.TenderSearchRequest;
import com.smarttender.search.dto.response.TenderSearchResult;
import com.smarttender.search.service.TenderSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/tenders") @RequiredArgsConstructor
@Tag(name = "Tender Search", description = "Search, filter and retrieve tenders")
public class TenderSearchController {
    private final TenderSearchService searchService;

    @GetMapping("/search")
    @Operation(summary = "Search and filter tenders with pagination")
    public ResponseEntity<ApiResponse<PagedResponse<TenderSearchResult>>> search(
            @Valid @ModelAttribute TenderSearchRequest request) {
        return ResponseEntity.ok(ApiResponse.success(searchService.search(request)));
    }

    @GetMapping("/{tenderId}")
    @Operation(summary = "Get tender by ID")
    public ResponseEntity<ApiResponse<TenderSearchResult>> getById(@PathVariable String tenderId) {
        return ResponseEntity.ok(ApiResponse.success(searchService.getById(tenderId)));
    }
}
