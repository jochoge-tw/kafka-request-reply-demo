package com.ochoge.messaging.messaging.publisher;

/**
 * Implemented by service components which publish and collate request-reply messages.
 */
@FunctionalInterface
public interface RequestReplyMessagePublisher {

    /**
     * Publishes the specified request message using the associated message parameters.
     *
     * @param requestMessage    The request message
     * @param messageParameters The message parameters.
     * @param <S>               The request message type
     * @param <T>               The response message type
     * @return the reply payload
     */
    <S, T> T publishWithReply(S requestMessage, RequestReplyMessageParameters<S, T> messageParameters);
}
