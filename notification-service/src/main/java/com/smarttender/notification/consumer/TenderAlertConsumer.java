package com.smarttender.notification.consumer;
import com.smarttender.common.constants.KafkaTopics;
import com.smarttender.common.event.NotificationAlertEvent;
import com.smarttender.notification.service.impl.EmailNotificationSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
@Slf4j @Component @RequiredArgsConstructor
public class TenderAlertConsumer {
    private final EmailNotificationSender emailSender;
    @KafkaListener(topics = KafkaTopics.TENDER_ALERTS, groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ConsumerRecord<String, NotificationAlertEvent> record) {
        NotificationAlertEvent event = record.value();
        log.info("Alert received tenderId={} for {} users", event.getTenderId(),
                event.getMatchedUserIds() != null ? event.getMatchedUserIds().size() : 0);
        if (event.getMatchedUserIds() == null) return;
        for (Long userId : event.getMatchedUserIds()) {
            // In production, look up user email from user service or pass it in the event
            String recipientEmail = "user" + userId + "@example.com";
            emailSender.sendNotification(userId, recipientEmail, event);
        }
    }
}
