package com.jpmc.midascore;

import com.jpmc.midascore.foundation.Transaction;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MidasKafkaListener {

    @KafkaListener(
        topics = "${general.kafka-topic}",
        groupId = "midas-group"
    )
    public void listen(Transaction transaction) {
        // just receive — do nothing
        System.out.println(transaction);
    }
}
