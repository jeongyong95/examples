package com.joojeongyong.simplekafka.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaProducer {
    private static final String TOPIC = "myTopic";
    private final KafkaTemplate<String, String> template;

    public void sendMessage(String message) {
        log.info("message produced : {}", message);
        this.template.send(TOPIC, message);
    }
}
