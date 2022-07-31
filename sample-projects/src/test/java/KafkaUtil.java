import java.util.HashMap;
import java.util.Map;

public class KafkaUtil {
    public static String KAFKA_CONFIG_BOOTSTRAP_SERVERS = "bootstrap.servers";
    public static String KAFKA_CONFIG_KEY_SERIALIZER = "key.serializer";
    public static String KAFKA_CONFIG_VALUE_SERIALIZER = "value.serializer";
    public static String KAFKA_CONFIG_GROUP_ID = "group.id";
    public static String KAFKA_CONFIG_ENABLE_AUTO_COMMIT = "enable.auto.commit";
    public static String KAFKA_CONFIG_KEY_DESERIALIZER = "key.deserializer";
    public static String KAFKA_CONFIG_VALUE_DESERIALIZER = "value.deserializer";
    public static String KAFKA_CONFIG_AUTO_OFFSET_RESET = "auto.offset.reset";
    public static String KAFKA_CONFIG_ACK = "acks";
    public static String KAFKA_CONFIG_LINGER_MS = "linger.ms";

    protected static Map<String, String> getDefaultProducerConfig() {
        Map<String, String> config = new HashMap<>();
        config.put(KAFKA_CONFIG_BOOTSTRAP_SERVERS, "localhost:9092");
        config.put(KAFKA_CONFIG_KEY_SERIALIZER, "org.apache.kafka.common.serialization.StringSerializer");
        config.put(KAFKA_CONFIG_VALUE_SERIALIZER, "org.apache.kafka.common.serialization.StringSerializer");
        config.put(KAFKA_CONFIG_ACK,"all");
        config.put(KAFKA_CONFIG_LINGER_MS, "1");
        return config;
    }

    protected static Map<String,String> getDefaultConsumerConfig(){
        Map<String, String> config = new HashMap<>();
        config.put(KAFKA_CONFIG_BOOTSTRAP_SERVERS, "localhost:9092");
        config.put(KAFKA_CONFIG_GROUP_ID, "test");
        config.put(KAFKA_CONFIG_ENABLE_AUTO_COMMIT, "true");
        config.put(KAFKA_CONFIG_KEY_DESERIALIZER,
                "org.apache.kafka.common.serialization.StringDeserializer");
        config.put(KAFKA_CONFIG_VALUE_DESERIALIZER,
                "org.apache.kafka.common.serialization.StringDeserializer");
        config.put(KAFKA_CONFIG_AUTO_OFFSET_RESET, "latest");
        return config;
    }

}
