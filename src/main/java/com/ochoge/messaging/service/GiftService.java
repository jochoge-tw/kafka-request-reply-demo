package com.ochoge.messaging.service;

import com.ochoge.messaging.domain.Gift;
import com.ochoge.messaging.domain.ReciprocalGift;
import com.ochoge.messaging.messaging.publisher.RequestReplyMessageParameters;
import com.ochoge.messaging.messaging.publisher.RequestReplyMessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class GiftService {
    private final RequestReplyMessagePublisher messagePublisher;

    public ReciprocalGift sendAndAwait(Gift gift) {
        RequestReplyMessageParameters<Gift, ReciprocalGift> parameters = RequestReplyMessageParameters.<Gift, ReciprocalGift>builder()
                .requestTopic(Gift.TOPIC)
                .requestTimeout(Duration.ofSeconds(5))
                .replyTopic(ReciprocalGift.TOPIC)
                .replyTimeout(Duration.ofSeconds(10))
                .replyTypeReference(ReciprocalGift.class)
                .build();
        return messagePublisher.publishWithReply(gift, parameters);
    }
}
