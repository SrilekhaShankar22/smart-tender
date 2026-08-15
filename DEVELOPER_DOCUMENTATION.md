# Smart Tender Tracker — Developer Documentation

## Table of Contents
1. [Architecture Overview](#architecture)
2. [Microservice Communication](#communication)
3. [Kafka Flow](#kafka)
4. [Database Schemas](#database)
5. [Design Patterns](#patterns)
6. [Setup Instructions](#setup)
7. [Deployment Guide](#deployment)
8. [Troubleshooting](#troubleshooting)

---

## 1. Architecture Overview {#architecture}

```
                    ┌─────────────────────────────────┐
                    │         Angular Frontend         │
                    │    (Dashboard / Search / Admin)  │
                    └──────────────┬──────────────────┘
                                   │ HTTP/REST
                    ┌──────────────▼──────────────────┐
                    │         AWS API Gateway          │
                    │   (Auth / Routing / Rate Limit)  │
                    └──┬──────┬──────┬──────┬─────────┘
                       │      │      │      │
              ┌────────▼─┐  ┌─▼────┐ ┌▼──────────┐  ┌▼─────────┐
              │auth-svc  │  │fetch │ │search-svc │  │profile  │
              │:8084     │  │:8081 │ │:8083      │  │:8086    │
              └────┬─────┘  └──┬───┘ └─────┬─────┘  └─────────┘
                   │           │           │
              MySQL/Redis    Kafka       Elasticsearch
                           ┌──▼───┐
                           │process│
                           │:8082  │
                           └──┬───┘
                        ┌─────▼──────┐
                        │notification│
                        │:8085       │
                        └────────────┘
```

**Principle:** Each service owns its data. No service calls another's database directly.
Communication is either REST (synchronous) or Kafka (asynchronous).

---

## 2. Microservice Communication {#communication}

### Synchronous (REST)
- Angular → auth-service: Login, Register, Token refresh
- Angular → search-service: Search tenders, Get tender by ID
- Angular → profile-service: CRUD saved searches, notification prefs
- Angular → fetch-service: Manual trigger (admin only)

### Asynchronous (Kafka)
- fetch-service → [tender.raw] → processing-service
- processing-service → [tender.processed] → search-service (index), notification-service
- processing-service → [tender.alerts] → notification-service (email)

---

## 3. Kafka Event Flow {#kafka}

```
eprocure.gov.in
      │
      │ HTTP GET (Jsoup/WebClient)
      ▼
┌─────────────────┐
│ TenderFetchJob  │  Quartz fires every 30 min
│ (Quartz)        │
└────────┬────────┘
         │ TenderRawEvent
         ▼
  [Kafka: tender.raw]
         │
         ▼
┌────────────────────────────────┐
│    Processing Pipeline         │
│  1. ValidationHandler          │  Chain of Responsibility
│  2. NormalizationHandler       │
│  3. KeywordExtractionHandler   │
│  4. RelevanceScoringHandler    │
└────────────────────────────────┘
         │
    ┌────┴────┐
    │         │
    ▼         ▼
 MySQL    Elasticsearch
         (TenderDocument)
    │
    ▼
[Kafka: tender.processed]
    │
    ├──▶ search-service indexes it
    │
    └──▶ [Kafka: tender.alerts]  (if matches saved search profiles)
              │
              ▼
         notification-service
              │
              ▼
           Email sent
```

**Kafka Topics:**
| Topic | Key | Value | Partitions |
|---|---|---|---|
| tender.raw | tenderId | TenderRawEvent | 3 |
| tender.processed | tenderId | TenderProcessedEvent | 3 |
| tender.alerts | tenderId | NotificationAlertEvent | 3 |
| tender.notifications | userId | audit log | 1 |

---

## 4. Database Schemas {#database}

### smart_tender_auth
```
users          → id, email, password, first_name, last_name, enabled
roles          → id, name (ROLE_USER, ROLE_ADMIN)
user_roles     → user_id, role_id (many-to-many)
refresh_tokens → id, token, user_id, expires_at, revoked
```

### smart_tender_fetch
```
fetch_log      → id, job_id, status, pages_fetched, new_tenders, error_message
fetched_tender → id, tender_id, content_hash, title, org, closing_date, source_type
```

### smart_tender_processing
```
processed_tender → id, tender_id, status, relevance_score, is_duplicate, es_indexed
processing_log   → id, tender_id, status, error_message, processing_time_ms
```

### smart_tender_profiles
```
saved_search            → id, user_id, name, keywords, org, category, source_type, alert_enabled
notification_preferences → id, user_id, email_enabled, frequency, min_relevance_score
user_profiles           → id, user_id, display_name, company, city, state
```

### smart_tender_notifications
```
notification_log → id, user_id, tender_id, channel, recipient, status, retry_count
```

---

## 5. Design Patterns {#patterns}

### Chain of Responsibility — Processing Pipeline
Each handler processes one concern and passes to the next:
```
ValidationHandler(1) → NormalizationHandler(2) → KeywordExtractionHandler(3) → RelevanceScoringHandler(4)
```
Adding a new step = create new class extending `AbstractProcessingHandler`, annotate `@Order(N)`.

### Strategy Pattern — CAPTCHA Solving
```java
app.captcha.strategy=mock       // MockCaptchaSolverStrategy (dev)
app.captcha.strategy=twocaptcha // TwoCaptchaSolverStrategy (prod)
```
Spring auto-selects via `@ConditionalOnProperty`.

### Template Method — NotificationSender
`sendNotification()` defines the algorithm. Subclasses implement `buildSubject()`, `buildBody()`, `doSend()`.
Add SMS: extend `NotificationSender`, implement the 3 abstract methods.

### Builder Pattern — Elasticsearch Queries
`TenderQueryBuilder.buildSearchRequest()` constructs complex bool queries from a `TenderSearchRequest` object.
Clean, readable, testable.

---

## 6. Setup Instructions {#setup}

### Prerequisites
- Java 21 (Temurin recommended): https://adoptium.net
- Maven 3.9+
- Docker Desktop
- Node 20+ (for Angular)
- IntelliJ IDEA (recommended)

### Step 1: Clone and configure
```bash
git clone https://github.com/SrilekhaShankar22/smart-tender.git
cd smart-tender
```

### Step 2: Start infrastructure
```bash
docker-compose up -d mysql kafka zookeeper redis elasticsearch kafka-ui kibana
# Wait ~60s for all services to be healthy
```

### Step 3: Verify infrastructure
```bash
# MySQL
mysql -h localhost -P 3306 -utender_user -ptender_pass -e "SHOW DATABASES;"

# Kafka UI
open http://localhost:8090

# Elasticsearch
curl http://localhost:9200/_cluster/health
```

### Step 4: Build
```bash
mvn clean install -DskipTests
```

### Step 5: Run services (in separate terminals)
```bash
cd auth-service            && mvn spring-boot:run  # :8084
cd tender-fetch-service    && mvn spring-boot:run  # :8081
cd tender-processing-service && mvn spring-boot:run # :8082
cd tender-search-service   && mvn spring-boot:run  # :8083
cd user-profile-service    && mvn spring-boot:run  # :8086
cd notification-service    && mvn spring-boot:run  # :8085
```

### Step 6: Run frontend
```bash
cd angular-frontend
npm install
npm start
# Opens http://localhost:4200
```

### Step 7: Login
- URL: http://localhost:4200/auth/login
- Admin: admin@smarttender.com / Admin@123

### Step 8: Trigger first fetch
```bash
curl -X POST http://localhost:8081/api/v1/fetch/trigger
# OR via Admin panel in the UI
```

---

## 7. Deployment Guide {#deployment}

### Full Docker Compose
```bash
# Build all JARs first
mvn clean package -DskipTests
# Build and start everything
docker-compose up --build -d
```

### Kubernetes with Helm
```bash
# Create namespace
kubectl create namespace smart-tender

# Apply secrets
kubectl apply -f k8s/infra/secrets.yml

# Deploy with Helm
helm upgrade --install smart-tender ./helm/smart-tender \
  --namespace smart-tender \
  --set global.dockerHubUsername=YOUR_USERNAME \
  --set global.imageTag=latest

# Check pods
kubectl get pods -n smart-tender
```

---

## 8. Troubleshooting {#troubleshooting}

| Problem | Solution |
|---|---|
| Flyway migration fails | Check DB URL and schema exists (`docker/mysql/init/01_create_schemas.sql` runs on first start) |
| Kafka connection refused | Ensure `KAFKA_BOOTSTRAP_SERVERS=localhost:9092` not `kafka:9092` when running locally |
| Elasticsearch not found | Check `spring.elasticsearch.uris=http://localhost:9200` in application.yml |
| JWT 401 errors | Ensure `JWT_SECRET` is the same across all services (min 256 bits) |
| No tenders in search | Trigger a fetch: `POST localhost:8081/api/v1/fetch/trigger`, check Kafka UI for `tender.raw` messages |
| Port conflicts | Change `server.port` in each service's `application.yml` |

---

## 9. Future Enhancements

- Add SMS notifications via Twilio
- Implement GeM Bid value extraction from detail pages
- Add AI-based tender relevance scoring (OpenAI API)
- Multi-language support (Hindi)
- Mobile app (Angular PWA)
- Elasticsearch ML for auto-categorization
- Slack/Teams webhook notifications
