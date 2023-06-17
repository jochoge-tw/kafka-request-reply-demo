package com.ochoge.messaging.controller;

import com.ochoge.messaging.domain.Gift;
import com.ochoge.messaging.domain.ReciprocalGift;
import com.ochoge.messaging.service.GiftService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/gifts")
@RequiredArgsConstructor
public class GiftController {
    private final GiftService giftService;

    @PostMapping
    public ResponseEntity<ReciprocalGift> sendAndReceive(
            @RequestBody Gift gift) {
        ReciprocalGift reply = giftService.sendAndAwait(gift);
        return ResponseEntity.ok(reply);
    }
}
