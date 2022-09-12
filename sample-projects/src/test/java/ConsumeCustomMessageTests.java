import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ConsumeCustomMessageTests {

    private ConsumerRecords consume_records(KafkaConsumer consumer, long timestamp) {
        Map<TopicPartition, Long> partitionTimestampMap =
                (Map<TopicPartition, Long>) consumer.assignment()
                        .stream()
                        .collect(Collectors.toMap(tp -> tp, tp -> timestamp));
        Map<TopicPartition, OffsetAndTimestamp> offsetMap
                = consumer.offsetsForTimes(partitionTimestampMap);
        for (Map.Entry<TopicPartition, OffsetAndTimestamp> entry : offsetMap.entrySet()) {
            consumer.seek(entry.getKey(), entry.getValue().offset());
        }
        return consumer.poll(Duration.ofMillis(500));
    }

    @Test
    void consume_specific_offset_by_time() {
        String topic = Util.getRandomTopicName();

        KafkaProducer producer = new KafkaProducer(KafkaUtil.getDefaultProducerConfig());
        IntStream.range(0, 10)
                .mapToObj(String::valueOf)
                .map(msg -> new ProducerRecord(topic, msg))
                .forEach(item -> {
                    Util.sleep(1000);
                    producer.send(item);
                });
        producer.close();

        long timestamp = System.currentTimeMillis()-5000;

        Map<String, String> cfg = KafkaUtil.getDefaultConsumerConfig();
        cfg.put(KafkaUtil.KAFKA_CONFIG_AUTO_OFFSET_RESET, KafkaUtil.OFFSET_RESET_EARLIEST);

        KafkaConsumer consumer = new KafkaConsumer(cfg);
        consumer.subscribe(Arrays.asList(topic));
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1500));
        Assertions.assertEquals(10, records.count());



        records = consume_records(consumer,timestamp);
        Assertions.assertEquals(5, records.count());

    }


    @Test
    void consume_specific_offset_by_offset() {
        String topic = Util.getRandomTopicName();

        KafkaProducer producer = new KafkaProducer(KafkaUtil.getDefaultProducerConfig());
        IntStream.range(0, 10)
                .mapToObj(String::valueOf)
                .map(msg -> new ProducerRecord(topic, msg))
                .forEach(producer::send);
        producer.close();

        Map<String, String> cfg = KafkaUtil.getDefaultConsumerConfig();
        cfg.put(KafkaUtil.KAFKA_CONFIG_AUTO_OFFSET_RESET, KafkaUtil.OFFSET_RESET_EARLIEST);

        KafkaConsumer consumer = new KafkaConsumer(cfg);
        consumer.subscribe(Arrays.asList(topic));
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1500));
        Assertions.assertEquals(10, records.count());


        consumer.seek(new TopicPartition(topic,0),5);
        records = consumer.poll(Duration.ofMillis(1500));
        Assertions.assertEquals(5, records.count());

    }
}
