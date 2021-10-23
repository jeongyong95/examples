package com.joojeongyong.simplekafka.controller;

import com.joojeongyong.simplekafka.kafka.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/kafka")
@RestController
public class KafkaController {
    private static final String RESPONSE_STRING = "SUCCESS";
    private final KafkaProducer producer;

    @PostMapping
    public ResponseEntity<String> sendMessage(@RequestParam String message) {
        this.producer.sendMessage(message);
        return ResponseEntity.ok(RESPONSE_STRING);
    }
}