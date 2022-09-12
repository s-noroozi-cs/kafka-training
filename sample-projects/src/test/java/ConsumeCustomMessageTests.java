import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ConsumeCustomMessageTests {

    private void consume_records(KafkaConsumer consumer, long epochSeconds) {
        Map<TopicPartition, Long> partitionTimestampMap =
                (Map<TopicPartition, Long>) consumer.assignment()
                        .stream()
                        .collect(Collectors.toMap(tp -> tp, tp -> epochSeconds));
        Map<TopicPartition, OffsetAndTimestamp> offsetMap
                = consumer.offsetsForTimes(partitionTimestampMap);
        for (Map.Entry<TopicPartition, OffsetAndTimestamp> entry : offsetMap.entrySet()) {
            consumer.seek(entry.getKey(), entry.getValue().offset());
        }
    }

    @Test
    void consume_specific_offset() {

        //        Long oneHourEarlier = Instant.now().atZone(ZoneId.systemDefault())
//                .minusHours(1).toEpochSecond();

        String topic = Util.getRandomTopicName();

        KafkaProducer producer = new KafkaProducer(KafkaUtil.getDefaultProducerConfig());
        IntStream.range(0, 10)
                .mapToObj(String::valueOf)
                .map(msg -> new ProducerRecord(topic, msg))
                .forEach(item -> {
                    Thread.sleep(1500);
                });
        producer.close();

        Map<String, String> cfg = KafkaUtil.getDefaultConsumerConfig();
        cfg.put(KafkaUtil.KAFKA_CONFIG_AUTO_OFFSET_RESET, KafkaUtil.OFFSET_RESET_EARLIEST);

        KafkaConsumer consumer = new KafkaConsumer(cfg);
        consumer.subscribe(Arrays.asList(topic));
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(300));

    }
}
