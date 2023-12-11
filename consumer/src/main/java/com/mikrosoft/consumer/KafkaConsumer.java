package com.mikrosoft.consumer;

// Java Program to Illustrate Kafka Consumer

// package com.amiya.kafka.apachekafkaconsumer.consumer;

// Importing required classes
        import com.mikrosoft.consumer.dao.ArticleDAO;
        import lombok.extern.slf4j.Slf4j;
        import org.apache.kafka.clients.consumer.Consumer;
        import org.apache.kafka.clients.consumer.ConsumerRecord;
        import org.springframework.kafka.annotation.KafkaListener;
        import org.springframework.kafka.support.Acknowledgment;
        import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaConsumer {

    @KafkaListener(groupId = "group01", topics = "topic01")
    public void onMessage(ConsumerRecord<String, Object> record, Acknowledgment acknowledgment, Consumer<?, ?> consumer) {
        System.out.println("here");
        log.info("Event on topic={}, payload={}", record.topic(), record.value());
        acknowledgment.acknowledge();

    }


}



