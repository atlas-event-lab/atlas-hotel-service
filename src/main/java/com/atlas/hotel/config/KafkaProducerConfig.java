package com.atlas.hotel.config;

import com.atlas.hotel.shared.messaging.EventTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Kafka producer configuration.
 * KafkaTemplate and ProducerFactory are auto-configured from application.yml
 * (spring.kafka.producer.*). This class declares all hotel.* topics owned by
 * Hotel Service; KafkaAdmin creates them on startup if they do not exist (topics.md).
 */
@Configuration
public class KafkaProducerConfig {

    @Bean
    public NewTopic hotelCreatedTopic() {
        return TopicBuilder.name(EventTopics.HOTEL_CREATED)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic hotelUpdatedTopic() {
        return TopicBuilder.name(EventTopics.HOTEL_UPDATED)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic hotelDeletedTopic() {
        return TopicBuilder.name(EventTopics.HOTEL_DELETED)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
