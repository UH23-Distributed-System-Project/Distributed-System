package kafkaadmin;

import com.jcraft.jsch.JSchException;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartition;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class KafkaAmin {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // Controllers: 43.157.66.25:9093,43.157.66.25:9095,43.157.66.25:9097
        Properties props = new Properties();
        String bootstrapServers = "43.131.12.169:9092,43.131.14.163:9092,43.131.14.163:9094";
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        try(AdminClient adminClient = AdminClient.create(props)) {
            while(true) {
                System.out.print("> ");
                String command = scanner.nextLine();

                if(command.startsWith("list consumer-group-offsets")) {
                    printConsumerGroupOffsets(adminClient, command.split(" ")[2]);

                } else if(command.startsWith("list offsets")) {
                    String[] a = command.split(" ");
                    printOffsets(adminClient, a[2], Integer.parseInt(a[3]));
                } else {
                    switch(command){
                        case "list topics":
                            printTopics(adminClient);
                            break;
                        case "list consumer-groups":
                            printConsumerGroups(adminClient);
                            break;
                        case "list brokers":
                            break;
                        case "start broker-monitor":
                            runBrokerMonitor(adminClient);
                            break;
                        case "describe topics":
                            printTopicDescription(adminClient);
                            break;
                        case "list transactions":
                            printTransactions(adminClient);
                            break;
                        case "quit":
                            System.exit(0);
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

    private static void printConsumerGroups(AdminClient adminClient) throws ExecutionException, InterruptedException {
        ListConsumerGroupsResult listConsumerGroupsResult = adminClient.listConsumerGroups();

        Collection<ConsumerGroupListing> consumerGroupListings = listConsumerGroupsResult.all().get();
        for(ConsumerGroupListing listing : consumerGroupListings) {
            System.out.println(listing.groupId());
        }
    }

    private static void printTopicDescription(AdminClient adminClient) throws ExecutionException, InterruptedException {
        Collection<TopicListing> listings;

        listings = getTopicListing(adminClient, false);
        List<String> topics = listings.stream().map(TopicListing::name).toList();
        DescribeTopicsResult result = adminClient.describeTopics(topics);
        result.topicNameValues().forEach((key, value) -> {
            try {
                System.out.println(key + ": " + value.get());
            } catch(InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    private static Collection<TopicListing> getTopicListing(AdminClient client, boolean isInternal) throws ExecutionException, InterruptedException {
        ListTopicsOptions options = new ListTopicsOptions();
        options.listInternal(isInternal);
        return client.listTopics(options).listings().get();
    }

    private static void runBrokerMonitor(AdminClient adminClient) {
        Timer timer = new Timer();
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                DescribeClusterOptions describeClusterOptions = new DescribeClusterOptions();
                DescribeClusterResult describeClusterResult = adminClient.describeCluster(describeClusterOptions);

                // Get cluster ID and print to stdout
                String clusterID;
                try {
                    clusterID = describeClusterResult.clusterId().get();
                    System.out.println("Cluster ID: " + clusterID);

                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println();

                // Get info about nodes in cluster and print to stdout
                try {
                    Collection<Node> nodes = describeClusterResult.nodes().get();
                    for(Node node : nodes) {
                        System.out.println("Node ID: " + node.id());
                        System.out.println("Host: " + node.host());
                        System.out.println("Port: " + node.port());
                        System.out.println("Rack: " + node.rack());
                        System.out.println("Is empty: " + node.isEmpty());
                        System.out.println();
                    }
                    System.out.println("--------------------------------\n");
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        timer.scheduleAtFixedRate(tt, 0, 10000);
    }

    public static void printTopics(AdminClient adminClient) throws ExecutionException, InterruptedException {
        ListTopicsResult listTopicsResult = adminClient.listTopics();
        Set<String> names = listTopicsResult.names().get();
        for (String name : names) {
            System.out.println(name);
        }
    }

    public static void printConsumerGroupOffsets(AdminClient adminClient, String groupID) throws ExecutionException, InterruptedException {
        ListConsumerGroupOffsetsResult listConsumerGroupOffsetsResult = adminClient.listConsumerGroupOffsets(groupID);
        Map<String, Map<TopicPartition, OffsetAndMetadata>> idk = listConsumerGroupOffsetsResult.all().get();

        idk.forEach((key, value) -> {
            System.out.print(key + ": ");
            value.forEach((k, v) -> {
                System.out.println(k + ": " + v + ", ");
            });
        });
    }

    public static void runSSHCommand(String command) throws JSchException, IOException {
        SSHConnection sshConnection = new SSHConnection();
        sshConnection.execute(command);
    }

    public static void printOffsets(AdminClient adminClient, String topic, int offset) throws ExecutionException, InterruptedException {
        TopicPartition topicPartition = new TopicPartition(topic, offset);
        ListOffsetsOptions options = new ListOffsetsOptions();

        Instant timestamp = Instant.now();

        // Perform the offsetsForTimes operation
        OffsetSpec offsetSpec = OffsetSpec.forTimestamp(timestamp.toEpochMilli());
        ListOffsetsResult result = adminClient.listOffsets(Collections.singletonMap(topicPartition, offsetSpec));
        result.all().get().forEach((key, value) -> {
            System.out.println(key + ": " + "Offset: " + value.offset());
        });
    }

    public static void printTransactions(AdminClient adminClient) throws ExecutionException, InterruptedException {
        Collection<TransactionListing> result = adminClient.listTransactions().all().get();

        for(TransactionListing t : result) {
            System.out.println(t);
        }
    }

    public void printProducerDetails(AdminClient adminClient, String topic, int partition) {

    }
}
