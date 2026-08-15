package com.smarttender.fetch;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
/**
 * tender-fetch-service — Scrapes eprocure.gov.in, deduplicates, publishes to Kafka.
 * Port: 8081 | Swagger: http://localhost:8081/swagger-ui.html
 */
@SpringBootApplication
@EnableRetry
public class TenderFetchServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(TenderFetchServiceApplication.class, args);
    }
}
