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
        String topicName = Util.getRandomTopicName();
        KafkaProducer producer = new KafkaProducer(KafkaUtil.getDefaultProducerConfig());
        Assertions.assertThrows(SerializationException.class,
                () -> producer.send(new ProducerRecord(topicName, makeNewLogModel())));
    }

    @Test
    void test_java_serializer() {
        String topicName = Util.getRandomTopicName();
        Map config = KafkaUtil.getDefaultProducerConfig();
        config.put(KafkaUtil.KAFKA_CONFIG_VALUE_SERIALIZER,
                com.kafka.config.MyCustomSerialize.class.getName());
        KafkaProducer producer = new KafkaProducer(config);
        producer.send(new ProducerRecord(topicName, makeNewLogModel()));
        producer.close();
    }

    @Test
    void test_java_serialize_custom_exception() {
        String topicName = Util.getRandomTopicName();
        Map config = KafkaUtil.getDefaultProducerConfig();
        config.put(KafkaUtil.KAFKA_CONFIG_VALUE_SERIALIZER,
                com.kafka.config.MyCustomSerialize.class.getName());
        KafkaProducer producer = new KafkaProducer(config);
        Assertions.assertThrows(LogModelSerializeException.class,
                () -> producer.send(new ProducerRecord(topicName, LocalDateTime.now())));

    }


    @Test
    void test_json_serializer() {
        String topicName = Util.getRandomTopicName();
        Map config = KafkaUtil.getDefaultProducerConfig();
        config.put(KafkaUtil.KAFKA_CONFIG_VALUE_SERIALIZER,
                com.kafka.config.MyJsonSerializer.class.getName());
        KafkaProducer producer = new KafkaProducer(config);
        producer.send(new ProducerRecord(topicName, makeNewLogModel()));
        producer.close();
    }

    @Test
    void test_consume_latest() {
        String topic = Util.getRandomTopicName();

        KafkaProducer producer = new KafkaProducer(KafkaUtil.getDefaultProducerConfig());
        IntStream.range(1, 10)
                .mapToObj(i -> "sample message-" + i)
                .map(msg -> new ProducerRecord(topic, msg))
                .forEach(producer::send);
        producer.close();

        KafkaConsumer consumer = new KafkaConsumer(KafkaUtil.getDefaultConsumerConfig());
        consumer.subscribe(Arrays.asList(topic));
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(300));
        consumer.unsubscribe();
        consumer.close();

        Assertions.assertEquals(0, records.count());
    }

    @Test
    void test_consume_earliest() {
        String topicName = Util.getRandomTopicName();

        KafkaProducer producer = new KafkaProducer(KafkaUtil.getDefaultProducerConfig());
        IntStream.range(1, 10)
                .mapToObj(i -> "sample message-" + i * 2)
                .map(msg -> new ProducerRecord(topicName, msg))
                .forEach(producer::send);
        producer.close();


        Map consumerCfg = KafkaUtil.getDefaultConsumerConfig();
        consumerCfg.put(KafkaUtil.KAFKA_CONFIG_AUTO_OFFSET_RESET, KafkaUtil.OFFSET_RESET_EARLIEST);
        KafkaConsumer consumer = new KafkaConsumer(consumerCfg);
        consumer.subscribe(Arrays.asList(topicName));
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
        consumer.unsubscribe();
        consumer.close();

        Assertions.assertTrue(records.count() > 0);
    }


}
