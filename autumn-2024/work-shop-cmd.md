docker images  | grep kafka

docker run --rm -it --name kafka apache/kafka:3.7.0

docker exec -it kafka bash

netstat -tupln 

docker run --rm -it --name kafka -p 9092:9092 apache/kafka:3.7.0


offset explorer

/opt/kafka/bin/kafka-consul-producer.sh --bootstrap-server localhost:9092 --topic test

