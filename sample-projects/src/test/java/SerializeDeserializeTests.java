import com.model.LogLevel;
import com.model.LogModel;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.SerializationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class SerializeDeserializeTests {

    private LogModel makeNewLogModel() {
        return new LogModel(LocalDateTime.now(),
                "test-serialize",
                LogLevel.INFO,
                "this is a test message.");
    }

    private Map<String, String> getDefaultConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("bootstrap.servers", "localhost:9092");
        config.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        return config;
    }

    @Test
    void test_java_serializer() {
        Map config = getDefaultConfig();
        config.put("value.serializer",
                com.kafka.config.MyCustomSerialize.class.getName());
        KafkaProducer producer = new KafkaProducer(config);
        producer.send(new ProducerRecord("test", makeNewLogModel()));
        producer.close();
    }

    @Test
    void test_string_serializer() {
        Map config = getDefaultConfig();
        config.put("value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        KafkaProducer producer = new KafkaProducer(config);
        Assertions.assertThrows(SerializationException.class,
                () -> producer.send(new ProducerRecord("test", makeNewLogModel())));
    }

    @Test
    void test_json_serializer() {
        Map config = getDefaultConfig();
        config.put("value.serializer",
                com.kafka.config.MyJsonSerializer.class.getName());
        KafkaProducer producer = new KafkaProducer(config);
        producer.send(new ProducerRecord("test", makeNewLogModel()));
        producer.close();
    }

}
