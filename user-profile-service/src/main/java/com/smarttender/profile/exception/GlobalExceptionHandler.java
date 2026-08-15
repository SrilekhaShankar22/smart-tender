package com.smarttender.profile.exception;

import com.smarttender.common.dto.ApiResponse;
import com.smarttender.common.exception.ResourceNotFoundException;
import com.smarttender.common.exception.SmartTenderException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(SmartTenderException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(SmartTenderException ex) {
        return ResponseEntity.badRequest().body(ApiResponse.error(ex.getErrorCode(), ex.getMessage()));
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ex.getErrorCode(), ex.getMessage()));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity.internalServerError().body(ApiResponse.error("INTERNAL_ERROR", "An unexpected error occurred"));
    }
}
