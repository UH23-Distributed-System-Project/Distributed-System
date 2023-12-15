package com.example.springboot001.kafka;

import com.example.springboot001.service.KafkaConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KafkaRunner implements Runnable {

    @Autowired
    private KafkaConsumerService consumerService;

    public KafkaRunner(KafkaConsumerService service) {
        this.consumerService = service;
    }

    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();
        System.out.println("===============TreadName:" + threadName);
        consumerService.pollMessagesInSingleThread();
    }
}
