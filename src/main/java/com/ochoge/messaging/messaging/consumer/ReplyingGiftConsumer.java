package com.ochoge.messaging.messaging.consumer;

import com.ochoge.messaging.domain.Gift;
import com.ochoge.messaging.domain.ReciprocalGift;
import com.ochoge.messaging.utils.MessageUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReplyingGiftConsumer {
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @SneakyThrows
    @KafkaListener(topics = Gift.TOPIC, batch = "false")
    public void consumer(ConsumerRecord<String, Object> consumerRecord) {
        Object rawGift = consumerRecord.value();
        log.info("Received message {}", rawGift);
        Gift gift = objectMapper.readValue(String.valueOf(rawGift), Gift.class);
        ReciprocalGift reciprocalGift = ReciprocalGift.giftFor("Josh", gift);

        ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(ReciprocalGift.TOPIC, reciprocalGift);
        MessageUtils.getHeaderValues(consumerRecord, KafkaHeaders.CORRELATION_ID).forEach(headerValue -> producerRecord.headers().add(KafkaHeaders.CORRELATION_ID, headerValue));
        producerRecord.headers().add(KafkaHeaders.KEY, MessageUtils.getHashBasedUuidFor(reciprocalGift).getBytes(StandardCharsets.UTF_8));
        producerRecord.headers().add(KafkaHeaders.TIMESTAMP, String.valueOf(System.currentTimeMillis()).getBytes(StandardCharsets.UTF_8));
        kafkaTemplate.send(producerRecord);
    }
}