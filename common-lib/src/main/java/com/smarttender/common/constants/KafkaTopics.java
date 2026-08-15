package com.smarttender.common.constants;
/** Central definition of all Kafka topic names used across services. */
public final class KafkaTopics {
    private KafkaTopics() {}
    public static final String TENDER_RAW         = "tender.raw";
    public static final String TENDER_PROCESSED   = "tender.processed";
    public static final String TENDER_ALERTS      = "tender.alerts";
    public static final String TENDER_NOTIFICATIONS = "tender.notifications";
}
