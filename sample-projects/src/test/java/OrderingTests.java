import com.kafka.config.FixedPartitioner;
import lombok.NoArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.Cluster;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.*;
import java.util.stream.IntStream;

public class OrderingTests {
    @Test
    void test_keep_order_with_single_partition() {
        String topic = Util.getRandomTopicName();

        KafkaProducer producer = new KafkaProducer(KafkaUtil.getDefaultProducerConfig());
        IntStream.range(0, 10)
                .mapToObj(String::valueOf)
                .map(msg -> new ProducerRecord(topic, msg))
                .forEach(producer::send);
        producer.close();

        Map<String, String> cfg = KafkaUtil.getDefaultConsumerConfig();
        cfg.put(KafkaUtil.KAFKA_CONFIG_AUTO_OFFSET_RESET, KafkaUtil.OFFSET_RESET_EARLIEST);
        List<Integer> items = new ArrayList();
        KafkaConsumer consumer = new KafkaConsumer(cfg);
        consumer.subscribe(Arrays.asList(topic));
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(300));
        records.forEach(i -> items.add(Integer.parseInt(i.value())));
        consumer.unsubscribe();
        consumer.close();

        for (int i = 0; i < 10; i++) {
            Assertions.assertEquals(i, items.get(i));
        }
    }

    @Test
    void test_order_with_multiple_partitions_with_random_key() {
        String topicName = Util.getRandomTopicName();
        int partitions = 10;
        KafkaUtil.createTopic(topicName,partitions);

        KafkaProducer producer = new KafkaProducer(KafkaUtil.getDefaultProducerConfig());
        IntStream.range(0, 10)
                .mapToObj(i -> new ProducerRecord(topicName, String.valueOf(i),String.valueOf(i)))
                .forEach(producer::send);
        producer.close();

        Map<String, String> cfg = KafkaUtil.getDefaultConsumerConfig();
        cfg.put(KafkaUtil.KAFKA_CONFIG_AUTO_OFFSET_RESET, KafkaUtil.OFFSET_RESET_EARLIEST);
        List<Integer> items = new ArrayList();
        KafkaConsumer consumer = new KafkaConsumer(cfg);
        consumer.subscribe(Arrays.asList(topicName));
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(300));
        records.forEach(i -> items.add(Integer.parseInt(i.value())));
        consumer.unsubscribe();
        consumer.close();

        boolean hasOrder = true;
        for (int i=0; i<10; i++){
            hasOrder = hasOrder && items.get(i) == i;
        }

        Assertions.assertEquals(false,hasOrder);

    }

    @Test
    void test_order_with_key(){
        String topicName = Util.getRandomTopicName();
        int partitions = 10;
        KafkaUtil.createTopic(topicName,partitions);

        KafkaProducer producer = new KafkaProducer(KafkaUtil.getDefaultProducerConfig());
        IntStream.range(0, 10)
                .mapToObj(i -> new ProducerRecord(topicName,"keep-order",String.valueOf(i)))
                .forEach(producer::send);
        producer.close();

        Map<String, String> cfg = KafkaUtil.getDefaultConsumerConfig();
        cfg.put(KafkaUtil.KAFKA_CONFIG_AUTO_OFFSET_RESET, KafkaUtil.OFFSET_RESET_EARLIEST);
        List<Integer> items = new ArrayList();
        KafkaConsumer consumer = new KafkaConsumer(cfg);
        consumer.subscribe(Arrays.asList(topicName));
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(300));
        records.forEach(i -> items.add(Integer.parseInt(i.value())));
        consumer.unsubscribe();
        consumer.close();

        for (int i = 0; i < 10; i++) {
            Assertions.assertEquals(i, items.get(i));
        }
    }

    @Test
    void test_order_with_custom_biz(){
        String topicName = Util.getRandomTopicName();
        int partitions = 10;
        KafkaUtil.createTopic(topicName,partitions);

        Map producerConfig = KafkaUtil.getDefaultProducerConfig();
        producerConfig.put(KafkaUtil.KAFKA_PARTITIONER_CLASS, FixedPartitioner.class.getName());

        KafkaProducer producer = new KafkaProducer(producerConfig);
        IntStream.range(0, 10)
                .mapToObj(i -> new ProducerRecord(topicName,String.valueOf(i),String.valueOf(i)))
                .forEach(producer::send);
        producer.close();

        Map<String, String> cfg = KafkaUtil.getDefaultConsumerConfig();
        cfg.put(KafkaUtil.KAFKA_CONFIG_AUTO_OFFSET_RESET, KafkaUtil.OFFSET_RESET_EARLIEST);
        List<Integer> items = new ArrayList();
        KafkaConsumer consumer = new KafkaConsumer(cfg);
        consumer.subscribe(Arrays.asList(topicName));
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(300));
        records.forEach(i -> items.add(Integer.parseInt(i.value())));
        consumer.unsubscribe();
        consumer.close();

        for (int i = 0; i < 10; i++) {
            Assertions.assertEquals(i, items.get(i));
        }
    }




}
