package com.smarttender.fetch;

import com.smarttender.common.enums.SourceType;
import com.smarttender.common.event.TenderRawEvent;
import com.smarttender.fetch.config.AppProperties;
import com.smarttender.fetch.entity.FetchLog;
import com.smarttender.fetch.kafka.TenderRawEventPublisher;
import com.smarttender.fetch.repository.FetchLogRepository;
import com.smarttender.fetch.repository.FetchedTenderRepository;
import com.smarttender.fetch.service.GemPortalScraper;
import com.smarttender.fetch.service.impl.TenderFetchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TenderFetchServiceTest {

    @Mock GemPortalScraper scraper;
    @Mock TenderRawEventPublisher publisher;
    @Mock FetchLogRepository fetchLogRepo;
    @Mock FetchedTenderRepository fetchedTenderRepo;
    @Mock AppProperties props;
    @InjectMocks TenderFetchService fetchService;

    @BeforeEach
    void setUp() {
        AppProperties.Gem gem = new AppProperties.Gem();
        gem.setFetchPageSize(2);
        when(props.getGem()).thenReturn(gem);

        AppProperties.Kafka kafka = new AppProperties.Kafka();
        when(props.getKafka()).thenReturn(kafka);
    }

    @Test
    void processAndPublish_skips_duplicates() {
        TenderRawEvent event = TenderRawEvent.builder()
                .tenderId("123").contentHash("abc").title("Test Tender")
                .organisationName("Test Org").sourceType(SourceType.CENTRAL)
                .fetchedAt(LocalDateTime.now()).build();

        when(fetchedTenderRepo.existsByTenderId("123")).thenReturn(true);

        FetchLog log = FetchLog.builder().id(1L).jobId("job1")
                .startedAt(LocalDateTime.now()).status(FetchLog.FetchStatus.RUNNING).build();

        int count = fetchService.processAndPublish(List.of(event), log);

        verify(publisher, never()).publish(any());
        assert count == 0;
    }

    @Test
    void processAndPublish_publishes_new_tenders() {
        TenderRawEvent event = TenderRawEvent.builder()
                .tenderId("999").contentHash("xyz").title("New Tender")
                .organisationName("New Org").sourceType(SourceType.CENTRAL)
                .fetchedAt(LocalDateTime.now()).build();

        when(fetchedTenderRepo.existsByTenderId("999")).thenReturn(false);
        when(fetchedTenderRepo.existsByContentHash("xyz")).thenReturn(false);
        when(fetchedTenderRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        FetchLog log = FetchLog.builder().id(1L).jobId("job1")
                .startedAt(LocalDateTime.now()).status(FetchLog.FetchStatus.RUNNING).build();

        int count = fetchService.processAndPublish(List.of(event), log);

        verify(publisher, times(1)).publish(event);
        assert count == 1;
    }
}
