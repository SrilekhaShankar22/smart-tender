package com.smarttender.notification.service;
import com.smarttender.common.event.NotificationAlertEvent;
import com.smarttender.notification.entity.NotificationLog;
import com.smarttender.notification.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;

/**
 * Template Method Pattern:
 * sendNotification() defines the algorithm skeleton.
 * Subclasses implement buildSubject(), buildBody(), doSend().
 */
@Slf4j @RequiredArgsConstructor
public abstract class NotificationSender {
    protected final NotificationLogRepository logRepo;

    public final void sendNotification(Long userId, String recipient, NotificationAlertEvent event) {
        String subject = buildSubject(event);
        String body    = buildBody(event);
        NotificationLog logEntry = NotificationLog.builder()
                .userId(userId).tenderId(event.getTenderId())
                .channel(getChannel()).recipient(recipient)
                .subject(subject).status("PENDING").build();
        logRepo.save(logEntry);
        try {
            doSend(recipient, subject, body);
            logEntry.setStatus("SENT");
            logEntry.setSentAt(LocalDateTime.now());
        } catch (Exception e) {
            log.error("Failed to send {} to {}: {}", getChannel(), recipient, e.getMessage());
            logEntry.setStatus("FAILED");
            logEntry.setErrorMessage(e.getMessage());
            logEntry.setRetryCount(logEntry.getRetryCount() + 1);
        } finally {
            logRepo.save(logEntry);
        }
    }
    protected abstract String getChannel();
    protected abstract String buildSubject(NotificationAlertEvent event);
    protected abstract String buildBody(NotificationAlertEvent event);
    protected abstract void doSend(String recipient, String subject, String body);
}
