package com.example.springboot001;

import com.example.springboot001.kafka.KafkaRunner;
import com.example.springboot001.service.KafkaConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.PostConstruct;

@EnableAsync
@SpringBootApplication
public class ConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }

    @Autowired
    private KafkaConsumerService consumerService;

    @PostConstruct
    public void init() {
        new Thread(new KafkaRunner(consumerService)).start();
    }



}
