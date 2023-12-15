package com.example.springboot001.kafka;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class ConsumerListener {

    private String[] TOPIC_NAME = {"Students","UniversityHelsinki","News","AcademicArticles","Sports","Others"};


//    @KafkaListener(topics ={"Students"}, containerFactory = "batchFactory")
//    public void dealMessageBatch(List<String> msgList) {
//
//    }



    //@KafkaListener(topics ={"Students"})
    public void dealMessage(String msg) {
        // Define a regular expression pattern
        if (StringUtils.isEmpty(msg))
            return;

        List<String> articles = new ArrayList<>();
        String[] arrays = msg.split(",");
        articles = Arrays.asList(arrays);
        /**
         * The inaugural lectures of the new professors,
         * University of Helsinki,
         * 12-12-2023,
         * https://www.helsinki.fi/en/about-us/university-helsinki/festivities-and-traditions/inaugural-lectures-new-professors
         */
    }
}
