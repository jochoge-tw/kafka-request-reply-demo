package com.ochoge.messaging.service;

import com.ochoge.messaging.domain.Greeting;
import com.ochoge.messaging.domain.GreetingReply;
import com.ochoge.messaging.messaging.publisher.RequestReplyMessageParameters;
import com.ochoge.messaging.messaging.publisher.RequestReplyMessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class GreetingService {
    private final RequestReplyMessagePublisher messagePublisher;

    public GreetingReply sendAndAwait(Greeting greeting) {
        RequestReplyMessageParameters<Greeting, GreetingReply> parameters = RequestReplyMessageParameters.<Greeting, GreetingReply>builder()
                .requestTopic(Greeting.TOPIC)
                .requestTimeout(Duration.ofSeconds(5))
                .replyTopic(GreetingReply.TOPIC)
                .replyTimeout(Duration.ofSeconds(10))
                .replyTypeReference(GreetingReply.class)
                .build();
        return messagePublisher.publishWithReply(greeting, parameters);
    }
}
