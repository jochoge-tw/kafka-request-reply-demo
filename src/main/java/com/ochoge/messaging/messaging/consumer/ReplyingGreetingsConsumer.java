package com.ochoge.messaging.messaging.consumer;

import com.ochoge.messaging.domain.Greeting;
import com.ochoge.messaging.domain.GreetingReply;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReplyingGreetingsConsumer {
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @KafkaListener(topics = Greeting.TOPIC, batch = "false")
    @SendTo
    public GreetingReply consumer(ConsumerRecord<String, Object> consumerRecord) {
        Object rawGreeting = consumerRecord.value();
        log.info("Received message {}", rawGreeting);
        Greeting greeting = objectMapper.readValue(String.valueOf(rawGreeting), Greeting.class);
        return GreetingReply.reply("Hola, comestas", greeting);
    }
}
