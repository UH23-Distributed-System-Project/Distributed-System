package com.example.springboot001.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Kafka消费者任务上下文
 */
public class KafkaConsumerContext {
    /**
     * 存放所有自己创建的Kafka消费者任务
     * key: groupId
     * value: kafka消费者任务
     */
    private static final Map<String, KafkaConsumer<?, ?>> consumerMap = new ConcurrentHashMap<>();

    /**
     * 存放所有定时任务的哈希表
     * key: groupId
     * value: 定时任务对象，用于定时执行kafka消费者的消息消费任务
     */
    private static final Map<String, ScheduledFuture<?>> scheduleMap = new ConcurrentHashMap<>();

    /**
     * 任务调度器，用于定时任务
     */
    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(24);

    /**
     * 添加一个Kafka消费者任务
     *
     * @param groupId  消费者的组名
     * @param consumer 消费者对象
     * @param <K>      消息键类型
     * @param <V>      消息值类型
     */
    public static <K, V> void addConsumerTask(String groupId, KafkaConsumer<K, V> consumer) {
        // 创建定时任务
        // 每隔1s拉取消息并处理
        ScheduledFuture<?> future = executor.scheduleAtFixedRate(() -> {
            // 拉取消息
            ConsumerRecords<K, V> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<K, V> record : records) {
                // 自定义处理每次拉取的消息
                System.out.println(record.value());
            }
        }, 0, 1, TimeUnit.SECONDS);
        // 将任务和存入对应的列表以后续管理
        consumerMap.put(groupId, consumer);
        scheduleMap.put(groupId, future);
    }

    /**
     * 移除Kafka消费者定时任务并关闭消费者订阅
     *
     * @param groupId 消费者的组名
     */
    public static void removeConsumerTask(String groupId) {
        if (!consumerMap.containsKey(groupId)) {
            return;
        }
        // 取出对应的消费者与任务，并停止
        KafkaConsumer<?, ?> consumer = consumerMap.get(groupId);
        ScheduledFuture<?> future = scheduleMap.get(groupId);
        consumer.close();
        future.cancel(true);
        // 移除列表中的消费者和任务
        consumerMap.remove(groupId);
        scheduleMap.remove(groupId);
    }
}
