package com.smarttender.processing.pipeline;
/** Chain of Responsibility: each handler processes then passes to next. */
public interface ProcessingHandler {
    void setNext(ProcessingHandler next);
    void handle(ProcessingContext context);
    int getOrder();
}
