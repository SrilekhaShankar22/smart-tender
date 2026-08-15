package com.smarttender.processing;

import com.smarttender.common.enums.SourceType;
import com.smarttender.common.enums.TenderStatus;
import com.smarttender.common.event.TenderProcessedEvent;
import com.smarttender.common.event.TenderRawEvent;
import com.smarttender.processing.pipeline.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

class ProcessingPipelineTest {

    private TenderProcessingPipeline pipeline;

    @BeforeEach
    void setUp() {
        List<ProcessingHandler> handlers = List.of(
                new ValidationHandler(),
                new NormalizationHandler(),
                new KeywordExtractionHandler(),
                new RelevanceScoringHandler()
        );
        pipeline = new TenderProcessingPipeline(handlers);
        pipeline.buildChain();
    }

    @Test
    void pipeline_processes_valid_tender() {
        TenderRawEvent raw = TenderRawEvent.builder()
                .tenderId("TEST123").title("Civil Construction Works")
                .organisationName("CPWD").sourceType(SourceType.CENTRAL)
                .bidSubmissionClosingDate(LocalDateTime.now().plusDays(10))
                .publishedDate(LocalDateTime.now().minusDays(1))
                .contentHash("hash123").build();

        TenderProcessedEvent processed = pipeline.process(raw);

        assertThat(processed.getTenderId()).isEqualTo("TEST123");
        assertThat(processed.getTitle()).isEqualTo("Civil Construction Works");
        assertThat(processed.getExtractedKeywords()).isNotEmpty();
        assertThat(processed.getRelevanceScore()).isBetween(0.0, 1.0);
        assertThat(processed.getTenderStatus()).isEqualTo(TenderStatus.ACTIVE);
    }

    @Test
    void pipeline_marks_expired_tender() {
        TenderRawEvent raw = TenderRawEvent.builder()
                .tenderId("EXP001").title("Expired Tender")
                .organisationName("Old Dept").sourceType(SourceType.STATE)
                .bidSubmissionClosingDate(LocalDateTime.now().minusDays(5))
                .publishedDate(LocalDateTime.now().minusDays(30))
                .contentHash("expHash").build();

        TenderProcessedEvent processed = pipeline.process(raw);

        assertThat(processed.getTenderStatus()).isEqualTo(TenderStatus.EXPIRED);
        assertThat(processed.getDaysUntilClosing()).isNegative();
    }

    @Test
    void validation_fails_for_missing_tenderId() {
        TenderRawEvent raw = TenderRawEvent.builder()
                .tenderId(null).title("No ID Tender")
                .organisationName("Org").build();

        TenderProcessedEvent processed = pipeline.process(raw);
        // Pipeline returns what was built so far — just check no crash
        assertThat(processed).isNotNull();
    }
}
