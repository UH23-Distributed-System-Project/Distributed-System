package com.mikrosoft.producer;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

public class Producer {
    private Map<String, List<String>> RECORDS;
    private String PATH = "producer\\src\\main\\resources\\articles.csv";
    private String SERVER;
    private Properties PROPS = new Properties();
    private KafkaProducer<String, String> producer;

    public Producer(String path, String server) {
        this.PATH = path;
        this.SERVER = server;
        RecordGenerator rg = new RecordGenerator(this.PATH);
        this.RECORDS = rg.getRecordsList();
        PROPS.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, SERVER);
        PROPS.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        PROPS.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producer = new KafkaProducer<>(PROPS);
    }

    public boolean send() {
        for (Map.Entry<String, List<String>> entry : RECORDS.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();
            for (String value : values) {
                ProducerRecord<String, String> producerRecord =
                        new ProducerRecord<>(key, key, value); // ProducerRecord<topic, key, value>
                System.out.println("Next record to be sent is: " + producerRecord);
                try {
                    RecordMetadata metadata = producer.send(producerRecord).get();
                    System.out.println(
                            "Message sent to：" + "topic-" + metadata.topic() + "|partition-"
                                    + metadata.partition() + "|offset-" + metadata.offset());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

}
