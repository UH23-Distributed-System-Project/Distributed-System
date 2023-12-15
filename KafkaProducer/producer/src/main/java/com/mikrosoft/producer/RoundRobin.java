package com.mikrosoft.producer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class RoundRobin {
    private final List<String> SERVER_LIST;
    private final AtomicInteger CURRENT_INDEX;
    private final static String PATH = "producer\\src\\main\\resources\\nextserverid.txt";

    public RoundRobin(List<String> serverList) {
        if (serverList == null || serverList.isEmpty()) {
            throw new IllegalArgumentException("Server list must not be empty");
        }
        int next = 0;
        try {
            next = readIndexFromFile(PATH);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } ;
        this.SERVER_LIST = serverList;
        this.CURRENT_INDEX = new AtomicInteger(next);
    }

    public void writeIndexToFile() throws IOException {
        FileWriter fileWriter = new FileWriter(PATH);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.println(this.CURRENT_INDEX.get());
        printWriter.close();
    }

    public String getNextServer() {
        int index = CURRENT_INDEX.getAndIncrement();
        System.out.println(index);
        int serverCount = SERVER_LIST.size();

        if (index >= serverCount) {
            CURRENT_INDEX.set(1);
            index = 0;
        }
        return SERVER_LIST.get(index);
    }

    private static int readIndexFromFile(String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        Scanner scanner = new Scanner(file);
        int number = scanner.nextInt();
        scanner.close();
        return number;
    }
}
