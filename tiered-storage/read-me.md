reference artice: 
    
    https://developers.redhat.com/articles/2023/11/22/getting-started-tiered-storage-apache-kafka#build_the_aiven_tiered_storage_plug_in


    docker compose up

    ${KAFKA_HOME}/bin/kafka-topics.sh --bootstrap-server localhost:9092 \
    --create --topic test \
    --partitions 1 --replication-factor 1 \
    --config remote.storage.enable=true \
    --config local.retention.ms=1000 \
    --config segment.bytes=1000000


    ${KAFKA_HOME}/bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic test

    ${KAFKA_HOME}/bin/kafka-producer-perf-test.sh --topic test \
    --producer-props bootstrap.servers=localhost:9092 \
    --num-records 1000 \
    --throughput -1 \
    --record-size 1024

    ${KAFKA_HOME}/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 \
    --topic test \
    --from-beginning \
    --max-messages 1