package com.smarttender.processing.consumer;
import com.smarttender.common.constants.KafkaTopics;
import com.smarttender.common.event.TenderRawEvent;
import com.smarttender.common.event.TenderProcessedEvent;
import com.smarttender.processing.entity.*;
import com.smarttender.processing.pipeline.TenderProcessingPipeline;
import com.smarttender.processing.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Slf4j @Component @RequiredArgsConstructor
public class TenderRawEventConsumer {
    private final TenderProcessingPipeline pipeline;
    private final ProcessedTenderRepository processedRepo;
    private final TenderDocumentRepository esRepo;
    private final KafkaTemplate<String, TenderProcessedEvent> kafkaTemplate;

    @KafkaListener(topics = KafkaTopics.TENDER_RAW, groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ConsumerRecord<String, TenderRawEvent> record) {
        TenderRawEvent raw = record.value();
        log.info("Received tenderId={} from partition={}", raw.getTenderId(), record.partition());
        long start = System.currentTimeMillis();
        try {
            if (processedRepo.existsByContentHash(raw.getContentHash())) {
                log.debug("DUPLICATE skipped: {}", raw.getTenderId()); return;
            }
            TenderProcessedEvent processed = pipeline.process(raw);
            saveToMySQL(processed);
            indexToElasticsearch(processed);
            publishProcessedEvent(processed);
            log.info("Processed tenderId={} in {}ms", raw.getTenderId(), System.currentTimeMillis()-start);
        } catch (Exception e) {
            log.error("Failed to process tenderId={}: {}", raw.getTenderId(), e.getMessage(), e);
        }
    }

    private void saveToMySQL(TenderProcessedEvent p) {
        processedRepo.save(ProcessedTender.builder()
                .tenderId(p.getTenderId()).contentHash(p.getContentHash())
                .title(p.getTitle()).organisationName(p.getOrganisationName())
                .sourceType(p.getSourceType() != null ? p.getSourceType().name() : null)
                .tenderStatus(p.getTenderStatus() != null ? p.getTenderStatus().name() : null)
                .relevanceScore(p.getRelevanceScore()).isDuplicate(p.isDuplicate())
                .closingDate(p.getBidSubmissionClosingDate())
                .processedAt(LocalDateTime.now()).esIndexed(false).build());
    }

    private void indexToElasticsearch(TenderProcessedEvent p) {
        TenderDocument doc = TenderDocument.builder()
                .id(p.getTenderId()).tenderId(p.getTenderId())
                .title(p.getTitle()).tenderRefNo(p.getTenderRefNo())
                .organisationName(p.getOrganisationName())
                .productCategory(p.getProductCategory())
                .sourceType(p.getSourceType() != null ? p.getSourceType().name() : null)
                .tenderStatus(p.getTenderStatus() != null ? p.getTenderStatus().name() : null)
                .publishedDate(p.getPublishedDate())
                .bidSubmissionClosingDate(p.getBidSubmissionClosingDate())
                .tenderOpeningDate(p.getTenderOpeningDate())
                .fullDescription(p.getFullDescription())
                .extractedKeywords(p.getExtractedKeywords())
                .relevanceScore(p.getRelevanceScore()).isDuplicate(p.isDuplicate())
                .daysUntilClosing(p.getDaysUntilClosing())
                .detailUrl(p.getDetailUrl()).contentHash(p.getContentHash())
                .processedAt(p.getProcessedAt()).build();
        esRepo.save(doc);
        log.debug("Indexed tenderId={} to Elasticsearch", p.getTenderId());
    }

    private void publishProcessedEvent(TenderProcessedEvent p) {
        kafkaTemplate.send(KafkaTopics.TENDER_PROCESSED, p.getTenderId(), p);
    }
}
