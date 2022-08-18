import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Arrays;
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

    @Test
    void check_default_consumer_isolation_level(){
        KafkaConsumer consumer = new KafkaConsumer(KafkaUtil.getDefaultConsumerConfig());
        System.out.printf("ok");
        consumer.metrics();

    }
}
