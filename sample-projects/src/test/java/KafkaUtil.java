import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.KafkaFuture;

import java.util.*;

public class KafkaUtil {
    public static String KAFKA_CONFIG_BOOTSTRAP_SERVERS = "bootstrap.servers";
    public static String KAFKA_CONFIG_KEY_SERIALIZER = "key.serializer";
    public static String KAFKA_CONFIG_VALUE_SERIALIZER = "value.serializer";
    public static String KAFKA_CONFIG_GROUP_ID = "group.id";
    public static String KAFKA_CONFIG_ENABLE_AUTO_COMMIT = "enable.auto.commit";
    public static String KAFKA_CONFIG_KEY_DESERIALIZER = "key.deserializer";
    public static String KAFKA_CONFIG_VALUE_DESERIALIZER = "value.deserializer";
    public static String KAFKA_CONFIG_AUTO_OFFSET_RESET = "auto.offset.reset";
    public static String KAFKA_CONFIG_ISOLATION_LEVEL = "isolation.level";
    public static String KAFKA_CONFIG_ACK = "acks";
    public static String KAFKA_CONFIG_LINGER_MS = "linger.ms";
    public static String KAFKA_CONFIG_BATCH_SIZE = "batch.size";
    public static String KAFKA_PARTITIONER_CLASS = "partitioner.class";
    public static String KAFKA_AUTO_COMMIT = "enable.auto.commit";


    public static String OFFSET_RESET_LATEST = "latest";
    public static String OFFSET_RESET_EARLIEST = "earliest";
    public static String ISOLATION_LEVEL_READ_COMMITTED = "read_committed";
    public static String ISOLATION_LEVEL_READ_UNCOMMITTED = "read_uncommitted";


    private static String BOOTSTRAP_SERVER = "localhost:9092";

    static Map<String, String> getDefaultProducerConfig() {
        Map<String, String> config = new HashMap<>();
        config.put(KAFKA_CONFIG_BOOTSTRAP_SERVERS, BOOTSTRAP_SERVER);
        config.put(KAFKA_CONFIG_KEY_SERIALIZER, "org.apache.kafka.common.serialization.StringSerializer");
        config.put(KAFKA_CONFIG_VALUE_SERIALIZER, "org.apache.kafka.common.serialization.StringSerializer");
        config.put(KAFKA_CONFIG_ACK, "all");
        return config;
    }

    KafkaConsumer getConsumer(String topic, String... cfgPairs) {
        Map config = KafkaUtil.getDefaultConsumerConfig();
        for (int i = 0; i < cfgPairs.length; i += 2) {
            config.put(cfgPairs[i], cfgPairs[i + 1]);
        }
        KafkaConsumer consumer = new KafkaConsumer(config);
        consumer.subscribe(List.of(topic));
        return consumer;
    }
    
    static Map<String, String> getDefaultConsumerConfig() {
        Map<String, String> config = new HashMap<>();
        config.put(KAFKA_CONFIG_BOOTSTRAP_SERVERS, BOOTSTRAP_SERVER);
        config.put(KAFKA_CONFIG_GROUP_ID, Util.getRandomConsumerGroupId());
        config.put(KAFKA_CONFIG_ENABLE_AUTO_COMMIT, "true");
        config.put(KAFKA_CONFIG_KEY_DESERIALIZER,
                "org.apache.kafka.common.serialization.StringDeserializer");
        config.put(KAFKA_CONFIG_VALUE_DESERIALIZER,
                "org.apache.kafka.common.serialization.StringDeserializer");
        config.put(KAFKA_CONFIG_AUTO_OFFSET_RESET, OFFSET_RESET_LATEST);
        return config;
    }

    private static Admin createAdminClient() {
        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        return Admin.create(properties);
    }

    static void createTopic(String topicName, int partitions) {
        try (Admin admin = createAdminClient()) {
            short replicationFactor = 1;
            NewTopic newTopic = new NewTopic(topicName, partitions, replicationFactor);
            CreateTopicsResult result = admin.createTopics(Collections.singleton(newTopic));
            KafkaFuture<Void> future = result.values().get(topicName);
            future.get();
        } catch (Throwable ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

}
