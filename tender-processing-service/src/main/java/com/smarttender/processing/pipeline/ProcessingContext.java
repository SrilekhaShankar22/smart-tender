package com.smarttender.processing.pipeline;
import com.smarttender.common.event.TenderRawEvent;
import com.smarttender.common.event.TenderProcessedEvent;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

/** Carries data through the processing pipeline. */
@Data @Builder
public class ProcessingContext {
    private TenderRawEvent rawEvent;
    private TenderProcessedEvent.TenderProcessedEventBuilder builder;
    @Builder.Default private List<String> errors = new ArrayList<>();
    @Builder.Default private boolean skipRemaining = false;
    public void addError(String error) { errors.add(error); }
    public boolean hasErrors() { return !errors.isEmpty(); }
}
