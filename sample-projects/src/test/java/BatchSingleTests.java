import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;

public class BatchSingleTests {

    ConsumerRecords consumeAllRecords(String topicName){
        Map consumerConfig = KafkaUtil.getDefaultConsumerConfig();
        consumerConfig.put(KafkaUtil.KAFKA_CONFIG_AUTO_OFFSET_RESET, KafkaUtil.OFFSET_RESET_EARLIEST);
        KafkaConsumer consumer = new KafkaConsumer(consumerConfig);
        consumer.subscribe(Arrays.asList(topicName));
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(300));
        consumer.unsubscribe();
        consumer.close();

        return records;
    }

    @Test
    void test_single_mode_produce_with_default_config(){
        String topicName = Util.getRandomTopicName();
        KafkaProducer producer = new KafkaProducer(KafkaUtil.getDefaultProducerConfig());
        ProducerRecord record = new ProducerRecord(topicName,"my-message");
        producer.send(record);

        Assertions.assertTrue(consumeAllRecords(topicName).count() > 0);

    }

    @Test
    void test_single_mode_produce_with_linger_time_3_seconds(){
        String topicName = Util.getRandomTopicName();
        Map config = KafkaUtil.getDefaultProducerConfig();
        config.put(KafkaUtil.KAFKA_CONFIG_LINGER_MS,"3000");
        KafkaProducer producer = new KafkaProducer(config);
        ProducerRecord record = new ProducerRecord(topicName,"my-message");
        producer.send(record);

        Assertions.assertEquals(0,consumeAllRecords(topicName).count());
    }

    @Test
    void test_single_mode_produce_with_linger_time_and_batch_size(){
        String topicName = Util.getRandomTopicName();
        Map config = KafkaUtil.getDefaultProducerConfig();
        config.put(KafkaUtil.KAFKA_CONFIG_LINGER_MS,"3000");
        config.put(KafkaUtil.KAFKA_CONFIG_BATCH_SIZE,"0");
        KafkaProducer producer = new KafkaProducer(config);
        ProducerRecord record = new ProducerRecord(topicName,"my-message");
        producer.send(record);
        producer.send(record);

        Assertions.assertEquals(0,consumeAllRecords(topicName).count());
    }
}
