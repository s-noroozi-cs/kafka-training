import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.IsolationLevel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;

public class TransactionalTests {
    @Test
    void check_isolation_level_read_uncommitted() {
        String topic = Util.getRandomTopicName();
        KafkaProducer producer = new KafkaProducer(KafkaUtil.getDefaultProducerConfig(
                ProducerConfig.TRANSACTIONAL_ID_CONFIG, Util.getRandomProducerTrxCfg()));

        KafkaConsumer consumer = KafkaUtil.getConsumer(topic,
                KafkaUtil.KAFKA_CONFIG_AUTO_OFFSET_RESET, KafkaUtil.OFFSET_RESET_EARLIEST);

        producer.initTransactions();
        producer.beginTransaction();
        producer.send(new ProducerRecord(topic, "A"));

        ConsumerRecords records = consumer.poll(Duration.ofMillis(400));
        Assertions.assertEquals(1, records.count());

        producer.abortTransaction();
    }

    @Test
    void check_isolation_level_read_committed() {
        String topic = Util.getRandomTopicName();
        KafkaProducer producer = new KafkaProducer(KafkaUtil.getDefaultProducerConfig(
                ProducerConfig.TRANSACTIONAL_ID_CONFIG, Util.getRandomProducerTrxCfg()));

        KafkaConsumer consumer = KafkaUtil.getConsumer(topic,
                KafkaUtil.KAFKA_CONFIG_AUTO_OFFSET_RESET, KafkaUtil.OFFSET_RESET_EARLIEST,
                ConsumerConfig.ISOLATION_LEVEL_CONFIG, IsolationLevel.READ_COMMITTED.name().toLowerCase()
        );

        producer.initTransactions();
        producer.beginTransaction();
        producer.send(new ProducerRecord(topic, "A"));

        ConsumerRecords records = consumer.poll(Duration.ofMillis(400));
        Assertions.assertEquals(0, records.count());
        producer.commitTransaction();

        producer.beginTransaction();
        producer.send(new ProducerRecord(topic, "A"));
        producer.commitTransaction();

        records = consumer.poll(Duration.ofMillis(400));
        Assertions.assertEquals(1, records.count());



    }
}
