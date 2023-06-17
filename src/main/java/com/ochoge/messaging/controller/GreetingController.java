package com.ochoge.messaging.controller;

import com.ochoge.messaging.domain.Greeting;
import com.ochoge.messaging.domain.GreetingReply;
import com.ochoge.messaging.service.GreetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/greetings")
@RequiredArgsConstructor
public class GreetingController {
    private final GreetingService greetingService;

    @PostMapping
    public ResponseEntity<GreetingReply> sendAndReceive(
            @RequestBody Greeting greeting) {
        GreetingReply reply = greetingService.sendAndAwait(greeting);
        return ResponseEntity.ok(reply);
    }
}
