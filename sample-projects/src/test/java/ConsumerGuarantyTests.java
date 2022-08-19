import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class ConsumerGuarantyTests {

    private void produce_10_records(String topicName){
        KafkaProducer producer = new KafkaProducer(KafkaUtil.getDefaultProducerConfig());
        IntStream.range(0, 10)
                .mapToObj(i -> new ProducerRecord(topicName,String.valueOf(i)))
                .forEach(producer::send);
        producer.close();
    }

    private KafkaConsumer getConsumerWithEarliestConfig(String topic){
        Map config = KafkaUtil.getDefaultConsumerConfig();
        config.put(KafkaUtil.KAFKA_CONFIG_AUTO_OFFSET_RESET,KafkaUtil.OFFSET_RESET_EARLIEST);
        KafkaConsumer consumer = new KafkaConsumer(config);
        consumer.subscribe(List.of(topic));
        return consumer;
    }


    @Test
    void check_default_consumer_begin_end_offset(){
        String topic = Util.getRandomTopicName();
        TopicPartition tp = new TopicPartition(topic,0);

        produce_10_records(topic);
        KafkaConsumer consumer = getConsumerWithEarliestConfig(topic);

        ConsumerRecords records = consumer.poll(Duration.ofMillis(300));
        Assertions.assertEquals(10,records.count());

        records = consumer.poll(Duration.ofMillis(300));
        Assertions.assertEquals(0,records.count());
        Assertions.assertEquals(0L,consumer.currentLag(tp).getAsLong());
        long beginOffset = ((Map<TopicPartition, Long>)consumer.beginningOffsets(List.of(tp))).get(tp);
        long endOffset = ((Map<TopicPartition, Long>)consumer.endOffsets(List.of(tp))).get(tp);

        Assertions.assertEquals(0,beginOffset);
        Assertions.assertEquals(10,endOffset);
    }
}
