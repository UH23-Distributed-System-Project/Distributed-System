package com.mikrosoft.producer;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.BufferedReader;
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
        try (BufferedReader br = new BufferedReader(new FileReader(PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String type = line.split(",")[2];
                String name = line.split(",")[0];
                String author = line.split(",")[1];
                String time = line.split(",")[3];
                String link = line.split(",")[4];

                String values = name + "," + author + "," + time + "," + link;
                addKeyValuePair(this.map, type, values);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addKeyValuePair(Map<String, List<String>> map, String key, String value) {
        map.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
    }
}
