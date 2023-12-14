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

public class KafkaAdmin {

    private final AdminClient adminClient;

    public KafkaAdmin(Properties props) {
        this.adminClient = AdminClient.create(props);
    }

    // Print consumer groups
    public void printConsumerGroups() throws ExecutionException, InterruptedException {
        ListConsumerGroupsResult listConsumerGroupsResult = adminClient.listConsumerGroups();

        Collection<ConsumerGroupListing> consumerGroupListings = listConsumerGroupsResult.all().get();
        for(ConsumerGroupListing listing : consumerGroupListings) {
            System.out.println(listing.groupId());
        }
    }

    // Print topic details
    public void printTopicDescription() throws ExecutionException, InterruptedException {
        Collection<TopicListing> listings;

        listings = getTopicListing(false);
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

    private Collection<TopicListing> getTopicListing(boolean isInternal) throws ExecutionException, InterruptedException {
        ListTopicsOptions options = new ListTopicsOptions();
        options.listInternal(isInternal);
        return adminClient.listTopics(options).listings().get();
    }

    // Starts the broker monitor, which periodically queries the cluster for active brokers
    // TODO: make broker-monitor automatically restart crashed broker
    public void runBrokerMonitor() {
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

    // Print topic names
    public void printTopics() throws ExecutionException, InterruptedException {
        ListTopicsResult listTopicsResult = adminClient.listTopics();
        Set<String> names = listTopicsResult.names().get();
        for (String name : names) {
            System.out.println(name);
        }
    }

    // Print the offset for topics of a specific consumer group
    public void printConsumerGroupOffsets(String groupID) throws ExecutionException, InterruptedException {
        ListConsumerGroupOffsetsResult listConsumerGroupOffsetsResult = adminClient.listConsumerGroupOffsets(groupID);
        Map<String, Map<TopicPartition, OffsetAndMetadata>> idk = listConsumerGroupOffsetsResult.all().get();

        idk.forEach((key, value) -> {
            System.out.print(key + ": ");
            value.forEach((k, v) -> System.out.println(k + ": " + v + ", "));
        });
        System.out.println();
    }

    public void runSSHCommand(String command) throws JSchException, IOException {
        SSHConnection sshConnection = new SSHConnection();
        sshConnection.execute(command);
    }

    // Print offsets for a specific partition
    public void printOffsets(String topic, int offset) throws ExecutionException, InterruptedException {
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

    // Print active transactions
    public void printTransactions() throws ExecutionException, InterruptedException {
        Collection<TransactionListing> result = adminClient.listTransactions().all().get();

        for(TransactionListing t : result) {
            System.out.println(t);
        }
    }

    // Print details of producers for all topics
    public void printProducerDetails() throws ExecutionException, InterruptedException {
        Collection<TopicListing> topicListings = getTopicListing(false);
        Collection<TopicPartition> topicPartitions = new ArrayList<>();

        for(TopicListing tl : topicListings) {
            topicPartitions.add(new TopicPartition(tl.name(), 0));
        }

        DescribeProducersResult result = adminClient.describeProducers(topicPartitions);

        result.all().get().forEach((key, value) -> {
            System.out.println(key + ": " + value.activeProducers());
        });
    }
}
