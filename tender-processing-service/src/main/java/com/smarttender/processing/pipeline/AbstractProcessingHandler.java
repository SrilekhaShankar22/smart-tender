package com.smarttender.processing.pipeline;
import lombok.extern.slf4j.Slf4j;
@Slf4j
public abstract class AbstractProcessingHandler implements ProcessingHandler {
    private ProcessingHandler next;
    @Override public void setNext(ProcessingHandler next) { this.next = next; }
    @Override
    public void handle(ProcessingContext context) {
        if (context.isSkipRemaining()) return;
        try { doHandle(context); }
        catch (Exception e) {
            log.error("[{}] Handler error: {}", getClass().getSimpleName(), e.getMessage());
            context.addError(getClass().getSimpleName() + ": " + e.getMessage());
        }
        if (next != null && !context.isSkipRemaining()) next.handle(context);
    }
    protected abstract void doHandle(ProcessingContext context);
}
