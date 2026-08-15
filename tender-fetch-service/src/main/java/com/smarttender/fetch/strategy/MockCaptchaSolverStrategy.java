package com.smarttender.fetch.strategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
@Slf4j @Component
@ConditionalOnProperty(name = "app.captcha.strategy", havingValue = "mock", matchIfMissing = true)
public class MockCaptchaSolverStrategy implements CaptchaSolverStrategy {
    @Override public String solve(String base64Image) {
        log.debug("[MOCK] CAPTCHA solve called"); return "MOCK_SOLVED";
    }
    @Override public String getStrategyName() { return "mock"; }
}
