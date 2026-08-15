package com.smarttender.fetch.kafka;
import com.smarttender.common.event.TenderRawEvent;
import com.smarttender.fetch.config.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
@Slf4j @Component @RequiredArgsConstructor
public class TenderRawEventPublisher {
    private final KafkaTemplate<String, TenderRawEvent> kafkaTemplate;
    private final AppProperties props;
    public void publish(TenderRawEvent event) {
        String topic = props.getKafka().getTopics().getTenderRaw();
        kafkaTemplate.send(topic, event.getTenderId(), event)
                .whenComplete((r, ex) -> {
                    if (ex != null) log.error("Publish failed tenderId={}: {}", event.getTenderId(), ex.getMessage());
                    else log.debug("Published tenderId={} to {}", event.getTenderId(), topic);
                });
    }
}
