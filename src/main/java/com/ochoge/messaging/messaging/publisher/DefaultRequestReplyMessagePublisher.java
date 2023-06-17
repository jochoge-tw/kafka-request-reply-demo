package com.ochoge.messaging.messaging.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultRequestReplyMessagePublisher implements RequestReplyMessagePublisher {

    private final ReplyingKafkaTemplate<String, Object, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public <S, T> T publishWithReply(S requestPayload, RequestReplyMessageParameters<S, T> messageParameters) {
        String requestTopic = messageParameters.getRequestTopic();
        ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(requestTopic, requestPayload);
        Map<String, String> requestHeaders = messageParameters.toMessageHeaders(requestPayload);
        requestHeaders.forEach((key, value) -> producerRecord.headers().add(key, value.getBytes(StandardCharsets.UTF_8)));

        log.info("Sending request {} with headers: {}", requestPayload, requestHeaders);
        RequestReplyFuture<String, Object, Object> replyFuture = kafkaTemplate.sendAndReceive(producerRecord);

        SendResult<String, Object> sendResult =
            replyFuture.getSendFuture().get(messageParameters.getRequestTimeout().toMillis(), TimeUnit.MILLISECONDS);
        log.info("Request sent with metadata {} ... awaiting reply", sendResult.getRecordMetadata());

        ConsumerRecord<String, Object> consumerRecord = replyFuture.get(messageParameters.getReplyTimeout().toMillis(), TimeUnit.MILLISECONDS);
        Object rawReply = consumerRecord.value();
        log.info("Received reply {}", rawReply);
        return objectMapper.readValue(String.valueOf(rawReply), messageParameters.getReplyTypeReference());
    }
}
