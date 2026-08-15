package com.smarttender.processing.pipeline;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
@Component @Order(1)
public class ValidationHandler extends AbstractProcessingHandler {
    @Override public int getOrder() { return 1; }
    @Override
    protected void doHandle(ProcessingContext ctx) {
        var raw = ctx.getRawEvent();
        if (raw.getTenderId() == null || raw.getTenderId().isBlank()) {
            ctx.addError("Missing tenderId"); ctx.setSkipRemaining(true); return;
        }
        if (raw.getTitle() == null || raw.getTitle().isBlank()) {
            ctx.addError("Missing title"); ctx.setSkipRemaining(true); return;
        }
    }
}
