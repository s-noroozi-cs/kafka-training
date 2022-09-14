import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class ConsumerGuarantyTests {

    private void produce_10_records(String topicName) {
        KafkaProducer producer = new KafkaProducer(KafkaUtil.getDefaultProducerConfig());
        IntStream.range(0, 10)
                .mapToObj(i -> new ProducerRecord(topicName, String.valueOf(i)))
                .forEach(producer::send);
        producer.close();
    }

    private long getBeginOffset(KafkaConsumer consumer, TopicPartition tp) {
        return ((Map<TopicPartition, Long>) consumer.beginningOffsets(List.of(tp))).get(tp);
    }

    private long getEndOffset(KafkaConsumer consumer, TopicPartition tp) {
        return ((Map<TopicPartition, Long>) consumer.endOffsets(List.of(tp))).get(tp);
    }

    @Test
    void check_consumer_begin_end_offset() {
        String topic = Util.getRandomTopicName();
        TopicPartition tp = new TopicPartition(topic, 0);

        produce_10_records(topic);
        KafkaConsumer consumer = KafkaUtil.getConsumer(topic,
                KafkaUtil.KAFKA_CONFIG_AUTO_OFFSET_RESET, KafkaUtil.OFFSET_RESET_EARLIEST);

        ConsumerRecords records = consumer.poll(Duration.ofMillis(300));
        Assertions.assertEquals(10, records.count());

        Assertions.assertEquals(0, getBeginOffset(consumer, tp));
        Assertions.assertEquals(10, getEndOffset(consumer, tp));


        records = consumer.poll(Duration.ofMillis(300));
        Assertions.assertEquals(0, records.count());
        Assertions.assertEquals(0L, consumer.currentLag(tp).getAsLong());

        Assertions.assertEquals(0, getBeginOffset(consumer, tp));
        Assertions.assertEquals(10, getEndOffset(consumer, tp));
    }


    @Test
    void check_consumer_manual_commit() {
        boolean commitData = false;

        String topic = Util.getRandomTopicName();
        TopicPartition tp = new TopicPartition(topic, 0);

        produce_10_records(topic);
        KafkaConsumer consumer = KafkaUtil.getConsumer(topic,
                KafkaUtil.KAFKA_CONFIG_AUTO_OFFSET_RESET, KafkaUtil.OFFSET_RESET_EARLIEST,
                KafkaUtil.KAFKA_AUTO_COMMIT, "false");

        String consumerGroupId = consumer.groupMetadata().groupId();

        ConsumerRecords records = consumer.poll(Duration.ofMillis(300));
        Assertions.assertEquals(10, records.count());

        Assertions.assertEquals(0, getBeginOffset(consumer, tp));
        Assertions.assertEquals(10, getEndOffset(consumer, tp));

        produce_10_records(topic);
        records = consumer.poll(Duration.ofMillis(300));
        Assertions.assertEquals(10, records.count());

        Assertions.assertEquals(0, getBeginOffset(consumer, tp));
        Assertions.assertEquals(20, getEndOffset(consumer, tp));

        if (commitData)
            consumer.commitSync();

        consumer.unsubscribe();
        consumer.close();

        consumer = KafkaUtil.getConsumer(topic,
                KafkaUtil.KAFKA_CONFIG_GROUP_ID, consumerGroupId,
                KafkaUtil.KAFKA_CONFIG_AUTO_OFFSET_RESET, KafkaUtil.OFFSET_RESET_EARLIEST,
                KafkaUtil.KAFKA_AUTO_COMMIT, "false");

        records = consumer.poll(Duration.ofMillis(300));
        Assertions.assertEquals(commitData ? 0 : 20, records.count());
    }
}
