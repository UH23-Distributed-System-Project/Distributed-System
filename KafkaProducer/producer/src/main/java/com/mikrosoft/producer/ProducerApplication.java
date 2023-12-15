package com.mikrosoft.producer;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.Random;

@SpringBootApplication
public class ProducerApplication {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		for (int i = 0; i < 3; i++) {
			if (i == 0) {
				System.out.print(
						"Please input:'operation,username,password' for user authentication, where operation is 'register' and 'login'\n");
			}
			String userInput = scanner.nextLine();
			Authenticator authenticator = new Authenticator(userInput);
			String result = authenticator.processRequest();
			if (result != "R" && result != "A") {
				if (i == 2) {
					System.exit(1);
				}
				System.out.print(result + ", please try again!\n");
			} else {
				System.out.print(result);
				break;
			}
		}
		scanner.close();
		List<String> servers = new ArrayList<>();
		// 2@128.214.9.25:9092,3@128.214.9.26:9092,1@128.214.11.91:9092
		servers.add("101@43.131.14.163:9092");
		servers.add("102@43.131.14.163:9094");
		servers.add("103@43.131.12.169:9092");
		SpringApplication.run(ProducerApplication.class, args);
		RoundRobin rr = new RoundRobin(servers);
		Producer producer =
				new Producer("producer\\src\\main\\resources\\articles.csv", rr.getNextServer());
		producer.send();
	}

	private static String getRandomElement(List<String> list) {
		// Check if the list is not empty
		if (list != null && !list.isEmpty()) {
			// Use Random to get a random index
			Random random = new Random();
			int randomIndex = random.nextInt(list.size());

			// Return the element at the random index
			return list.get(randomIndex);
		} else {
			// Return null or handle the case when the list is empty
			return null;
		}
	}
}
