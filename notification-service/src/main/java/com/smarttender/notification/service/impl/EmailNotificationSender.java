package com.smarttender.notification.service.impl;
import com.smarttender.common.event.NotificationAlertEvent;
import com.smarttender.notification.repository.NotificationLogRepository;
import com.smarttender.notification.service.NotificationSender;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j @Service
public class EmailNotificationSender extends NotificationSender {
    private final JavaMailSender mailSender;
    public EmailNotificationSender(NotificationLogRepository logRepo, JavaMailSender mailSender) {
        super(logRepo); this.mailSender = mailSender;
    }
    @Override protected String getChannel() { return "EMAIL"; }
    @Override
    protected String buildSubject(NotificationAlertEvent event) {
        return "[Smart Tender] New matching tender: " + event.getTenderTitle();
    }
    @Override
    protected String buildBody(NotificationAlertEvent event) {
        return String.format("""
            <html><body>
            <h2>New Tender Alert</h2>
            <p><strong>Title:</strong> %s</p>
            <p><strong>Organisation:</strong> %s</p>
            <p><strong>Reference:</strong> %s</p>
            <p><strong>Closing in:</strong> %d days</p>
            <p><strong>Relevance Score:</strong> %.2f</p>
            <p><strong>Matched Keywords:</strong> %s</p>
            <p><a href="%s">View Tender Details</a></p>
            <hr><p><small>Smart Tender Tracker — Unsubscribe from your profile settings</small></p>
            </body></html>
            """, event.getTenderTitle(), event.getOrganisationName(),
            event.getTenderRefNo(), event.getDaysUntilClosing(),
            event.getRelevanceScore(), event.getMatchedKeywords(),
            event.getDetailUrl() != null ? event.getDetailUrl() : "#");
    }
    @Override
    protected void doSend(String recipient, String subject, String body) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(msg);
            log.info("Email sent to {}", recipient);
        } catch (Exception e) { throw new RuntimeException("Email send failed: " + e.getMessage(), e); }
    }
}
