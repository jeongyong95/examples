package com.joojeongyong.simplekafka.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaConsumer {

    //    broker에게서 message를 받으려면 @kafkaListener를 message를 consume할 메서드에 달아준다.
    @KafkaListener(topics = {"myTopic"}, groupId = "oms")
    public void consume(String message) {
        log.info("message consumed : {}", message);
    }
}
