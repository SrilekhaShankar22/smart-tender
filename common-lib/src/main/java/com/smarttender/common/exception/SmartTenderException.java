package com.smarttender.common.exception;

/** Base runtime exception for all Smart Tender services. */
public class SmartTenderException extends RuntimeException {
    private final String errorCode;
    public SmartTenderException(String message) {
        super(message); this.errorCode = "SMART_TENDER_ERROR";
    }
    public SmartTenderException(String errorCode, String message) {
        super(message); this.errorCode = errorCode;
    }
    public SmartTenderException(String errorCode, String message, Throwable cause) {
        super(message, cause); this.errorCode = errorCode;
    }
    public String getErrorCode() { return errorCode; }
}
