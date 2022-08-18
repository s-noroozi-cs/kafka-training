import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.stream.IntStream;

public class ConsumerOffsetTests {

    private void produce_10_records(String topicName){
        KafkaProducer producer = new KafkaProducer(KafkaUtil.getDefaultProducerConfig());
        IntStream.range(0, 10)
                .mapToObj(i -> new ProducerRecord(topicName,String.valueOf(i)))
                .forEach(producer::send);
        producer.close();
    }

    @Test
    void check_consumer_offset(){
        String topicName = Util.getRandomTopicName();
        produce_10_records(topicName);

        KafkaConsumer consumer = new KafkaConsumer(KafkaUtil.getDefaultConsumerConfig());
        OptionalLong currentLag = consumer.currentLag(new TopicPartition(topicName,0));
        Assertions.assertEquals(10,currentLag.getAsLong());

        consumer.subscribe(Arrays.asList(topicName));
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(300));
        consumer.unsubscribe();
        consumer.close();


    }
}
