package com.smarttender.fetch.config;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
@Data @Component @ConfigurationProperties(prefix = "app")
public class AppProperties {
    private Gem gem = new Gem();
    private Captcha captcha = new Captcha();
    private Kafka kafka = new Kafka();
    private Scheduler scheduler = new Scheduler();
    @Data public static class Gem {
        private String baseUrl = "https://eprocure.gov.in";
        private int fetchPageSize = 10;
        private long requestDelayMs = 1500;
    }
    @Data public static class Captcha {
        private String strategy = "mock";
        private String twoCaptchaApiKey;
    }
    @Data public static class Kafka {
        private Topics topics = new Topics();
        @Data public static class Topics {
            private String tenderRaw = "tender.raw";
        }
    }
    @Data public static class Scheduler {
        private String fetchCron = "0 0/30 * * * ?";
        private boolean enabled = true;
    }
}
