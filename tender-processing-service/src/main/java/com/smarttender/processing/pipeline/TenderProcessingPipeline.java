package com.smarttender.processing.pipeline;
import com.smarttender.common.event.TenderProcessedEvent;
import com.smarttender.common.event.TenderRawEvent;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Slf4j @Component @RequiredArgsConstructor
public class TenderProcessingPipeline {
    private final List<ProcessingHandler> handlers;
    private ProcessingHandler firstHandler;

    @PostConstruct
    public void buildChain() {
        var sorted = handlers.stream().sorted(Comparator.comparingInt(ProcessingHandler::getOrder)).toList();
        for (int i = 0; i < sorted.size() - 1; i++) sorted.get(i).setNext(sorted.get(i + 1));
        firstHandler = sorted.isEmpty() ? null : sorted.get(0);
        log.info("Processing pipeline built with {} handlers", sorted.size());
    }

    public TenderProcessedEvent process(TenderRawEvent raw) {
        ProcessingContext ctx = ProcessingContext.builder()
                .rawEvent(raw)
                .builder(TenderProcessedEvent.builder().processedAt(LocalDateTime.now()))
                .build();
        if (firstHandler != null) firstHandler.handle(ctx);
        if (ctx.hasErrors()) log.warn("Processing errors for {}: {}", raw.getTenderId(), ctx.getErrors());
        return ctx.getBuilder().build();
    }
}
