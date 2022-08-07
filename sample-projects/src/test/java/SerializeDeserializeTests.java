import com.exception.LogModelSerializeException;
import com.model.LogLevel;
import com.model.LogModel;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.SerializationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

public class SerializeDeserializeTests {

    private LogModel makeNewLogModel() {
        return new LogModel(LocalDateTime.now(),
                "test-serialize",
                LogLevel.INFO,
                "this is a test message.");
    }

    @Test
    void test_string_serializer() {
        KafkaProducer producer = new KafkaProducer(KafkaUtil.getDefaultProducerConfig());
        Assertions.assertThrows(SerializationException.class,
                () -> producer.send(new ProducerRecord("test", makeNewLogModel())));
    }

    @Test
    void test_java_serializer() {
        Map config = KafkaUtil.getDefaultProducerConfig();
        config.put(KafkaUtil.KAFKA_CONFIG_VALUE_SERIALIZER,
                com.kafka.config.MyCustomSerialize.class.getName());
        KafkaProducer producer = new KafkaProducer(config);
        producer.send(new ProducerRecord("test", makeNewLogModel()));
        producer.close();
    }

    @Test
    void test_java_serialize_custom_exception() {
        Map config = KafkaUtil.getDefaultProducerConfig();
        config.put(KafkaUtil.KAFKA_CONFIG_VALUE_SERIALIZER,
                com.kafka.config.MyCustomSerialize.class.getName());
        KafkaProducer producer = new KafkaProducer(config);
        Assertions.assertThrows(LogModelSerializeException.class,
                () -> producer.send(new ProducerRecord("test", LocalDateTime.now())));

    }


    @Test
    void test_json_serializer() {
        Map config = KafkaUtil.getDefaultProducerConfig();
        config.put(KafkaUtil.KAFKA_CONFIG_VALUE_SERIALIZER,
                com.kafka.config.MyJsonSerializer.class.getName());
        KafkaProducer producer = new KafkaProducer(config);
        producer.send(new ProducerRecord("test", makeNewLogModel()));
        producer.close();
    }

    @Test
    void test_consume_latest() {
        KafkaConsumer consumer = new KafkaConsumer(KafkaUtil.getDefaultConsumerConfig());
        consumer.subscribe(Arrays.asList("test"));

        KafkaProducer producer = new KafkaProducer(KafkaUtil.getDefaultProducerConfig());
        IntStream.range(1, 10)
                .mapToObj(i -> "sample message-" + i * 2)
                .map(msg -> new ProducerRecord("test", msg))
                .forEach(producer::send);
        producer.close();

        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
        consumer.unsubscribe();
        consumer.close();

        Assertions.assertEquals(0, records.count());
    }

    @Test
    void test_consume_earliest() {
        AtomicBoolean hasRecord = new AtomicBoolean(false);

        Map consumerCfg = KafkaUtil.getDefaultConsumerConfig();
        consumerCfg.put(KafkaUtil.KAFKA_CONFIG_AUTO_OFFSET_RESET, "earliest");
        consumerCfg.put(KafkaUtil.KAFKA_CONFIG_GROUP_ID, "test");
        KafkaConsumer consumer = new KafkaConsumer(consumerCfg);
        consumer.subscribe(Arrays.asList("test"));
        Thread thread = new Thread(() -> {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                hasRecord.set(records.count() > 0);
                if (hasRecord.get())
                    break;
            }
        });
        thread.setDaemon(true);
        thread.start();

        KafkaProducer producer = new KafkaProducer(KafkaUtil.getDefaultProducerConfig());
        IntStream.range(1, 10)
                .mapToObj(i -> "sample message-" + i * 2)
                .map(msg -> new ProducerRecord("test", msg))
                .forEach(producer::send);
        producer.close();

        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < 500 && !hasRecord.get())
            ;

        consumer.unsubscribe();
        consumer.close();

        Assertions.assertTrue(hasRecord.get());
    }


}
