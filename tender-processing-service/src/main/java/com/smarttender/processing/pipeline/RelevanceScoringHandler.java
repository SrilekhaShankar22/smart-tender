package com.smarttender.processing.pipeline;

import com.smarttender.common.enums.TenderStatus;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
@Order(4)
public class RelevanceScoringHandler extends AbstractProcessingHandler {

    @Override
    public int getOrder() { return 4; }

    @Override
    protected void doHandle(ProcessingContext ctx) {
        var raw = ctx.getRawEvent();
        LocalDateTime now = LocalDateTime.now();
        double score = 0.5;
        long days = -1;

        if (raw.getBidSubmissionClosingDate() != null) {
            days = ChronoUnit.DAYS.between(now, raw.getBidSubmissionClosingDate());
            if (days < 0)        score -= 0.3;
            else if (days <= 3)  score += 0.3;
            else if (days <= 7)  score += 0.2;
            else if (days <= 30) score += 0.1;
        }

        if (raw.getPublishedDate() != null) {
            long publishedDaysAgo = ChronoUnit.DAYS.between(raw.getPublishedDate(), now);
            if (publishedDaysAgo <= 1)      score += 0.2;
            else if (publishedDaysAgo <= 7) score += 0.1;
        }

        score = Math.max(0.0, Math.min(1.0, score));

        TenderStatus status;
        if (days < 0)       status = TenderStatus.EXPIRED;
        else if (days <= 3) status = TenderStatus.CLOSING_SOON;
        else                status = TenderStatus.ACTIVE;

        ctx.getBuilder()
           .relevanceScore(score)
           .tenderStatus(status)
           .daysUntilClosing(days);
    }
}
