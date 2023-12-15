package com.mikrosoft.producer;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.FileReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;

public class Authenticator {
    private static final String DATABASE_PATH = "producer\\src\\main\\resources\\users.csv";
    private final UserCredentials USER;
    private static final String HASH_PATH = "producer\\src\\main\\resources\\hash.txt";
    private int VERSION;

    public Authenticator(String userinput) {
        // user input consists of 2 types:
        // register,username,password
        // login,username,password
        this.USER = new UserCredentials(userinput.split(",")[0], userinput.split(",")[1],
                userinput.split(",")[2]);
        System.out.print(userinput.split(",")[0].getClass());
        try (BufferedReader br = new BufferedReader(new FileReader(HASH_PATH))) {
            String[] values = br.readLine().split(",");
            try {
                int version = Integer.parseInt(values[0].trim());
                this.VERSION = version;
            } catch (NumberFormatException e) {
                // Handle the case where the first value is not a valid integer
                System.err.println("Error parsing integer: " + e.getMessage());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isValidFormat(String input) {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }

    public String processRequest() {
        String result = "";
        if (USER.operation.equals("login")) {
            System.out.println("Authenticating...");
            result = authenticateUser();
        } else if (USER.operation.equals("register")) {
            System.out.println("Registering...");
            result = registerUser();
        } else {
            result = "Invalid Operation";
        }
        return result;
    }

    public String registerUser() {
        // IF or IL or RU for failed register
        // Username,password for succeed register

        // Verify format of username and password
        if (!isValidFormat(USER.username) || !isValidFormat(USER.password)) {
            return "invalid input format, please follow the instructions"; // invalid format
        }
        // Verify length of username and password
        if (USER.username.length() >= 12 || USER.password.length() >= 12
                || USER.password.length() <= 3 || USER.username.length() <= 3) {
            return "invalid input length, length of username and password should be longer than 3 and shorter than 13."; // invalid
                                                                                                                         // length
        }
        try {
            String record = USER.username + "," + USER.password;
            try (CSVReader csvReader = new CSVReader(new FileReader(DATABASE_PATH))) {
                List<String[]> csvRecords = csvReader.readAll();
                if (usernameExists(csvRecords, record)) {
                    return "repeated username, please change another username.";
                }
            } catch (IOException | CsvException e) {
                e.printStackTrace();
            }
            // No repeated username, append to new line
            BufferedWriter writer = new BufferedWriter(new FileWriter(DATABASE_PATH, true));
            writer.write('\n' + USER.username + ',' + USER.password);
            writer.close();
            BufferedWriter writer1 = new BufferedWriter(new FileWriter(HASH_PATH, true));
            try {
                String sha256Hash = calculateSHA256(DATABASE_PATH);
                VERSION += 1;
                writer1.write(String.valueOf(VERSION) + "," + sha256Hash);
                writer1.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "R";
    }

    public void synchronize() {

    }

    public String authenticateUser() {
        // Verify format of username and password
        if (!isValidFormat(USER.username) || !isValidFormat(USER.password)) {
            return "invalid input format"; // invalid format
        }
        // Verify length of username and password
        if (USER.username.length() >= 12 || USER.password.length() >= 12
                || USER.password.length() <= 3 || USER.username.length() <= 3) {
            return "invalid input length"; // invalid length
        }
        // A for authenticated users
        String record = USER.username + "," + USER.password;
        try (CSVReader csvReader = new CSVReader(new FileReader(DATABASE_PATH))) {
            List<String[]> csvRecords = csvReader.readAll();
            if (recordExists(csvRecords, record)) {
                return "A";
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
        return "NA";
    }

    private static boolean recordExists(List<String[]> csvRecords, String recordToCheck) {
        for (String[] csvRecord : csvRecords) {
            // Assuming the records are comma-separated and have the same format
            String csvRecordString = String.join(",", csvRecord);

            if (csvRecordString.equals(recordToCheck)) {
                return true; // Record already exists
            }
        }
        return false; // Record does not exist
    }

    private static boolean usernameExists(List<String[]> csvRecords, String recordToCheck) {
        for (String[] csvRecord : csvRecords) {
            // Assuming the records are comma-separated and have the same format
            String csvRecordString = String.join(",", csvRecord);

            if (csvRecordString.split(",")[0].equals(recordToCheck.split(",")[0])) {
                return true; // Record already exists
            }
        }
        return false; // Record does not exist
    }

    private static String calculateSHA256(String filePath) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        try (DigestInputStream dis = new DigestInputStream(new FileInputStream(filePath), md)) {
            // Read the file content
            byte[] buffer = new byte[8192];
            while (dis.read(buffer) != -1) {
                // Reading the file updates the digest
            }
        }

        // Get the hash value
        byte[] hash = md.digest();

        // Convert the byte array to a hexadecimal string
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }

    public String getUser() {
        return this.USER.toString();
    }

    private class UserCredentials {
        private final String operation;
        private final String username;
        private final String password;

        public UserCredentials(String operation, String username, String password) {
            this.operation = operation;
            this.username = username;
            this.password = password;
        }

        @Override
        public String toString() {
            return "Operation type:" + operation + '\n' + "username:" + username + '\n'
                    + "password:" + password;
        }
    }
}
