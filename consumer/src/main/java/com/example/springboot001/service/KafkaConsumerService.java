package com.example.springboot001.service;

import com.example.springboot001.bean.Article;
import com.example.springboot001.kafka.KafkaDynamicConsumerFactory;
import com.example.springboot001.utils.RedisUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class KafkaConsumerService {
    @Autowired
    private KafkaDynamicConsumerFactory factory;

    @Resource
    private RedisUtils redisUtils;


    @Value("${spring.kafka.consumer.group-id}")
    private String defaultGroupId;

    public void pollMessagesInSingleThread() {
        try {
            KafkaConsumer<String, String> consumer = factory.createConsumer(defaultGroupId);
            //KafkaConsumerContext.addConsumerTask(groupId, consumer);

            // Subscribe to topics
            String[] array = {"Students","UniversityHelsinki","News","AcademicArticles","Sports","Others"};
            List<String> topicList = Arrays.asList(array);
            consumer.subscribe(topicList);

            // Start consuming messages in a loop
            try {
                while (true) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

                    for (ConsumerRecord<String, String> record : records) {
                        // Process the received record
                        System.out.println("======================Consumed record start==================");
                        System.out.println("topic = " + record.topic() + ", partition = " + record.partition() + ", offset = " + record.offset());
                        System.out.println("key = " + record.key() + ", value = " + record.value());

                        String topic = record.topic();
                        String articleStr = record.value();
                        Article newArt = new Article().constructBean(articleStr, topic);
                        redisUtils.lSet(topic, newArt);

                        // Manually commit the offset to mark the message as processed
                        TopicPartition topicPartition = new TopicPartition(record.topic(), record.partition());
                        OffsetAndMetadata offset = new OffsetAndMetadata(record.offset() + 1);
                        consumer.commitSync(Collections.singletonMap(topicPartition, offset));
                    }
                }
            } catch (Exception e) {
                //throw new RuntimeException(e);
            } finally {
                // Close the consumer when done
                consumer.close();
            }
        } catch (ClassNotFoundException e) {
            //throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
