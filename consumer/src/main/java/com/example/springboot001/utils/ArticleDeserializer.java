package com.example.springboot001.utils;

import com.example.springboot001.bean.Article;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;
import java.util.Map;

public class ArticleDeserializer implements Deserializer<Article> {
    private ObjectMapper objectMapper;

    @Override
    public void configure(Map configs, boolean isKey) {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Article deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, Article.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        this.objectMapper = null;
    }
}
