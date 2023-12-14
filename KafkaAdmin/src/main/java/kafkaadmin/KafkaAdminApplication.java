package kafkaadmin;

import org.apache.kafka.clients.admin.*;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class KafkaAdminApplication {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // Controllers: 43.157.66.25:9093,43.157.66.25:9095,43.157.66.25:9097
        Properties props = new Properties();
        String bootstrapServers = "43.131.12.169:9092,43.131.14.163:9092,43.131.14.163:9094";
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        KafkaAdmin kafkaAdmin = new KafkaAdmin(props);

        try {
            while(true) {
                System.out.print("> ");
                String command = scanner.nextLine();
                if(command.startsWith("list consumer-group-offsets")) {
                    kafkaAdmin.printConsumerGroupOffsets(command.split(" ")[2]);

                } else if(command.startsWith("list offsets")) {
                    String[] a = command.split(" ");
                    kafkaAdmin.printOffsets(a[2], Integer.parseInt(a[3]));
                } else {
                    switch(command){
                        case "list topics":
                            kafkaAdmin.printTopics();
                            break;
                        case "list consumer-groups":
                            kafkaAdmin.printConsumerGroups();
                            break;
                        case "list brokers":
                            break;
                        case "start broker-monitor":
                            kafkaAdmin.runBrokerMonitor();
                            break;
                        case "describe topics":
                            kafkaAdmin.printTopicDescription();
                            break;
                        case "list transactions":
                            kafkaAdmin.printTransactions();
                            break;
                        case "list producer-details":
                            kafkaAdmin.printProducerDetails();
                            break;
                        case "quit":
                            System.exit(0);
                            break;
                        default:
                            System.out.println("Unknown command: \"" + command + "\"");
                            break;
                    }
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
