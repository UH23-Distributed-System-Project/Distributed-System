package com.mikrosoft.producer;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RecordGenerator {
    private String PATH;
    private Map<String, List<String>> map;

    public RecordGenerator(String path) {
        this.PATH = path;
        this.buildRecordsList();
    }

    public Map<String, List<String>> getRecordsList() {
        return this.map;
    }

    public void buildRecordsList() {
        this.map = new HashMap<>();
        try (CSVReader csvReader = new CSVReader(new FileReader(PATH))) {
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                String type = line[2];
                String name = line[0];
                String author = line[1];
                String time = line[3];
                String link = line[4];

                String values = "Article: " + name + ", author:" + author + ", published on:" + time
                        + "\n\tLink to article: " + link;
                addKeyValuePair(this.map, type, values);
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
    }

    private static void addKeyValuePair(Map<String, List<String>> map, String key, String value) {
        map.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
    }
}
