package com.smarttender.fetch.strategy;
/** Strategy Pattern: Pluggable CAPTCHA solving. */
public interface CaptchaSolverStrategy {
    String solve(String base64Image);
    String getStrategyName();
}
