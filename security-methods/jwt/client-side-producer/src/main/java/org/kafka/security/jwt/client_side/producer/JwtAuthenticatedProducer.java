package org.kafka.security.jwt.client_side.producer;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class JwtAuthenticatedProducer {
  private static final String BOOTSTRAP_SERVERS = "localhost:9092";
  private static final String TOPIC = "secure-topic";

  public static Properties getProducerConfig(String jwtToken) {
    Properties props = new Properties();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

    // Security configuration
    props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
    props.put(SaslConfigs.SASL_MECHANISM, "OAUTHBEARER");
    props.put(
        SaslConfigs.SASL_JAAS_CONFIG,
        "org.apache.kafka.common.security.oauthbearer.OAuthBearerLoginModule required "
            + "oauth.token=\""
            + jwtToken
            + "\";");

    props.put(
        SaslConfigs.SASL_LOGIN_CALLBACK_HANDLER_CLASS, CustomLoginCallbackHandler.class.getName());

    return props;
  }

  public static void produceMessage(String message, String jwtToken) {
    try (KafkaProducer<String, String> producer =
        new KafkaProducer<>(getProducerConfig(jwtToken))) {

      ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, message);

      producer.send(
          record,
          (metadata, exception) -> {
            if (exception != null) {
              System.err.println("Error sending message: " + exception.getMessage());
            } else {
              System.out.println("Message sent successfully to " + metadata.topic());
            }
          });

      producer.flush();
    }
  }
}
