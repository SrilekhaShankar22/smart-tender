# 🏛️ Smart Tender Tracker

> Portfolio-grade enterprise microservices application for government tender discovery, filtering, and notification.

[![Java](https://img.shields.io/badge/Java-21-orange)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-green)](https://spring.io/projects/spring-boot)
[![Angular](https://img.shields.io/badge/Angular-17-red)](https://angular.dev)
[![Kafka](https://img.shields.io/badge/Kafka-7.6-black)](https://kafka.apache.org)

## Architecture

```
Angular UI → AWS API Gateway → Microservices → Kafka → Elasticsearch / MySQL / Redis

Kafka Topics:
  tender.raw         ← fetch-service publishes
  tender.processed   ← processing-service publishes
  tender.alerts      ← processing-service publishes (matched profiles)
  tender.notifications ← notification-service audit log
```

## Services

| Service | Port | Swagger | Description |
|---|---|---|---|
| auth-service | 8084 | :8084/swagger-ui.html | JWT auth, users, roles |
| tender-fetch-service | 8081 | :8081/swagger-ui.html | Quartz scheduler, web scraper |
| tender-processing-service | 8082 | — | Kafka consumer, pipeline, ES indexer |
| tender-search-service | 8083 | :8083/swagger-ui.html | Elasticsearch search APIs |
| user-profile-service | 8086 | :8086/swagger-ui.html | Saved searches, preferences |
| notification-service | 8085 | — | Email alerts via Kafka |
| angular-frontend | 4200 | — | Dashboard, search, admin UI |

## Quick Start

### Prerequisites
- Java 21, Maven 3.9+, Docker Desktop, Node 20+

### 1. Start Infrastructure
```bash
docker-compose up -d mysql kafka redis elasticsearch zookeeper kafka-ui kibana
```

### 2. Build All Services
```bash
mvn clean install -DskipTests
```

### 3. Run Services (each in a new terminal)
```bash
cd auth-service && mvn spring-boot:run
cd tender-fetch-service && mvn spring-boot:run
cd tender-processing-service && mvn spring-boot:run
cd tender-search-service && mvn spring-boot:run
cd user-profile-service && mvn spring-boot:run
cd notification-service && mvn spring-boot:run
```

### 4. Run Frontend
```bash
cd angular-frontend && npm install && npm start
```

### 5. Access
- 🌐 Frontend: http://localhost:4200
- 📚 Auth Swagger: http://localhost:8084/swagger-ui.html
- 📚 Search Swagger: http://localhost:8083/swagger-ui.html
- 📊 Kafka UI: http://localhost:8090
- 📊 Kibana: http://localhost:5601
- 🔑 Default admin: admin@smarttender.com / Admin@123

## Design Patterns Used

| Pattern | Where |
|---|---|
| Chain of Responsibility | Tender processing pipeline (validate→normalize→extract→score) |
| Strategy | CAPTCHA solver (mock/2captcha) |
| Template Method | NotificationSender (email/push/SMS channels) |
| Builder | Elasticsearch query construction |
| Observer | Kafka event-driven notification fan-out |
| Repository | Spring Data JPA for all data access |
| Circuit Breaker | Resilience4j on GeM portal HTTP calls |

## Data Source
- **Central Tenders**: https://eprocure.gov.in/cppp/latestactivetendersnew/cpppdata
- **State Tenders**: /mmpdata endpoint
- **GeM Tenders**: /gemdata endpoint
- Date format: `dd-MMM-yyyy hh:mm a`  |  No CAPTCHA on listing pages

## Database Schemas
- `smart_tender_auth` → auth-service
- `smart_tender_fetch` → tender-fetch-service
- `smart_tender_processing` → tender-processing-service
- `smart_tender_profiles` → user-profile-service
- `smart_tender_notifications` → notification-service

## Deployment
See `k8s/` for Kubernetes manifests and `helm/` for Helm chart.
CI/CD pipeline defined in `.github/workflows/ci-cd.yml`.
