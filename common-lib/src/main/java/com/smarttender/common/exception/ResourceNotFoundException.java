package com.smarttender.common.exception;

public class ResourceNotFoundException extends SmartTenderException {
    public ResourceNotFoundException(String resource, String field, Object value) {
        super("RESOURCE_NOT_FOUND", resource + " not found with " + field + " = " + value);
    }
}
