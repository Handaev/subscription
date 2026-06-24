package org.example.appsubscription.api.service.kafka;

import lombok.RequiredArgsConstructor;
import org.example.appsubscription.api.dto.InvalidateSubscriptionUserRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.producer.topicCache}")
    private String TOPIC_CACHE;

    public void sendMessageInvalidateCache(List<InvalidateSubscriptionUserRecord> deactivateUsers) {
        if (!CollectionUtils.isEmpty(deactivateUsers)) {
            kafkaTemplate.send(TOPIC_CACHE, deactivateUsers);
        }
    }
}