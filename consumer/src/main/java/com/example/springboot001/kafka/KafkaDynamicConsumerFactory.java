package com.example.springboot001.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.StickyAssignor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class KafkaDynamicConsumerFactory {

    @Value("${spring.kafka.bootstrap-servers}")
    private String consumerBootstrapServers;

    @Value("${spring.kafka.consumer.key-deserializer}")
    private String keyDeSerializerClassName;

    @Value("${spring.kafka.consumer.value-deserializer}")
    private String valueDeSerializerClassName;

    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String offsetResetReset;

    @Value("${spring.kafka.consumer.enable-auto-commit}")
    private boolean autoCommitEnable;

    /**
     * 创建一个Kafka消费者
     *
     * @param groupId 消费者组名
     * @return 消费者对象
     */
    public <K, V> KafkaConsumer<K, V> createConsumer(String groupId) throws ClassNotFoundException {
        Properties consumerProperties = new Properties();
        // 设定一些关于新的消费者的配置信息
        consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumerBootstrapServers);
        // 设定分区分配策略
        consumerProperties.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, StickyAssignor.class.getName());
        // 设定新的消费者的组名
        consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        // 设定反序列化方式
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, Class.forName(keyDeSerializerClassName));
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, Class.forName(valueDeSerializerClassName));
        // 设定数据消费模式
        consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offsetResetReset);
        // 设定提交offset模式
        consumerProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, autoCommitEnable);
        // 新建一个消费者
        KafkaConsumer<K, V> consumer = new KafkaConsumer<>(consumerProperties);
        return consumer;
    }
}
