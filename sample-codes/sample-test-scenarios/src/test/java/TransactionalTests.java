import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.IsolationLevel;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FilterOutputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class TransactionalTests {
    @Test
    void check_isolation_level_read_uncommitted() throws ExecutionException, InterruptedException {
        String topic = "test";
                //Util.getRandomTopicName();
        KafkaProducer producer = new KafkaProducer(KafkaUtil.getDefaultProducerConfig(

                ProducerConfig.TRANSACTIONAL_ID_CONFIG, Util.getRandomProducerTrxCfg()));

        //KafkaConsumer consumer = KafkaUtil.getConsumer(topic,
          //      KafkaUtil.KAFKA_CONFIG_AUTO_OFFSET_RESET, KafkaUtil.OFFSET_RESET_EARLIEST);

        producer.initTransactions();
        producer.beginTransaction();
        String msg = LocalDateTime.now().toString();

        Future<RecordMetadata> future =  producer.send(new ProducerRecord(topic, msg));
        System.out.println(" -------- " + msg + " -----------");
        System.out.println(future.get().toString());

//        try{
//            Thread.sleep(10_000);
//        }catch (Throwable ex){
//            ex.printStackTrace();
//        }

        //ConsumerRecords records = consumer.poll(Duration.ofMillis(400));
        //Assertions.assertEquals(1, records.count());

//        producer.abortTransaction();
        producer.commitTransaction();
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

    @Test
    void test_exactly_once_processing() {
        String inputTopic = Util.getRandomTopicName();
        String outputTopic = Util.getRandomTopicName();

        String inputConsumerGroup = Util.getRandomConsumerGroupId();
        String outputConsumerGroup = Util.getRandomConsumerGroupId();

        String inputData = "request data";
        String processKeyword = " --> processed";
        String outputData = inputData.concat(processKeyword);

        //exactly once at producer side
        KafkaProducer producer = new KafkaProducer(
                KafkaUtil.getDefaultProducerConfig(
                        ProducerConfig.TRANSACTIONAL_ID_CONFIG, Util.getRandomProducerTrxCfg(),
                        ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true"));
        producer.initTransactions();
        producer.beginTransaction();
        producer.send(new ProducerRecord<>(inputTopic, "request data"));
        producer.commitTransaction();

        //exactly once at consumer side
        KafkaConsumer consumer = new KafkaConsumer(
                KafkaUtil.getDefaultConsumerConfig(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false",
                        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, KafkaUtil.OFFSET_RESET_EARLIEST,
                        ConsumerConfig.GROUP_ID_CONFIG, inputConsumerGroup,
                        ConsumerConfig.ISOLATION_LEVEL_CONFIG, IsolationLevel.READ_COMMITTED.name().toLowerCase()));
        consumer.subscribe(List.of(inputTopic));
        ConsumerRecords records = consumer.poll(Duration.ofMillis(400));
        Assertions.assertEquals(1, records.count());
        ConsumerRecord record = (ConsumerRecord) records.records(new TopicPartition(inputTopic, 0)).get(0);
        Assertions.assertEquals(inputData, record.value());

        producer.beginTransaction();
        producer.send(new ProducerRecord(outputTopic, record.value() + processKeyword));

        Map<TopicPartition, OffsetAndMetadata> offsetsToCommit = new HashMap<>();
        for (TopicPartition partition : (Set<TopicPartition>) records.partitions()) {
            List<ConsumerRecord<String, String>> partitionedRecords = records.records(partition);
            long offset = partitionedRecords.get(partitionedRecords.size() - 1).offset();
            offsetsToCommit.put(partition, new OffsetAndMetadata(offset + 1));
        }

        producer.sendOffsetsToTransaction(offsetsToCommit, consumer.groupMetadata());
        producer.commitTransaction();

        records = consumer.poll(Duration.ofMillis(400));
        Assertions.assertEquals(0,records.count());

        consumer.subscribe(List.of(outputTopic));
        records = consumer.poll(Duration.ofMillis(400));
        Assertions.assertEquals(1,records.count());

        record = (ConsumerRecord) records.records(new TopicPartition(outputTopic, 0)).get(0);
        Assertions.assertEquals(outputData, record.value());
    }
}
