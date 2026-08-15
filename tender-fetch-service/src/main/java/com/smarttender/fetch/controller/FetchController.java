package com.smarttender.fetch.controller;
import com.smarttender.common.dto.ApiResponse;
import com.smarttender.fetch.entity.FetchLog;
import com.smarttender.fetch.repository.FetchLogRepository;
import com.smarttender.fetch.service.impl.TenderFetchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;
@RestController @RequestMapping("/api/v1/fetch") @RequiredArgsConstructor
@Tag(name = "Fetch Operations", description = "Manual trigger and monitoring")
public class FetchController {
    private final TenderFetchService fetchService;
    private final FetchLogRepository fetchLogRepo;

    @PostMapping("/trigger")
    @Operation(summary = "Manually trigger fetch cycle")
    public ResponseEntity<ApiResponse<Map<String,Object>>> trigger() {
        String jobId = "MANUAL_" + System.currentTimeMillis();
        long start = System.currentTimeMillis();
        fetchService.runFetchCycle(jobId);
        return ResponseEntity.ok(ApiResponse.success("Fetch complete",
                Map.of("jobId", jobId, "elapsedMs", System.currentTimeMillis()-start)));
    }

    @GetMapping("/status")
    @Operation(summary = "Last 10 fetch run logs")
    public ResponseEntity<ApiResponse<List<FetchLog>>> status() {
        return ResponseEntity.ok(ApiResponse.success(fetchLogRepo.findTop10ByOrderByStartedAtDesc()));
    }

    @GetMapping("/stats")
    @Operation(summary = "24h statistics")
    public ResponseEntity<ApiResponse<Map<String,Object>>> stats() {
        Long total = fetchLogRepo.sumNewTendersSince(LocalDateTime.now().minusHours(24));
        return ResponseEntity.ok(ApiResponse.success(Map.of("newTendersLast24h", total != null ? total : 0)));
    }
}
