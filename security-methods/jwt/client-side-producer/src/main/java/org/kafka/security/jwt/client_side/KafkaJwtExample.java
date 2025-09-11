package org.kafka.security.jwt.client_side;

import io.jsonwebtoken.Claims;
import org.kafka.security.jwt.client_side.producer.JwtAuthenticatedProducer;
import org.kafka.security.jwt.issuer.JwtTokenGenerator;

import java.time.LocalDateTime;

public class KafkaJwtExample {
  public static void main(String[] args) {
    // Generate JWT token
    String jwtToken =
        JwtTokenGenerator.generateToken("user1", new String[] {"producer", "consumer"});

    System.out.println("Generated JWT Token: " + jwtToken);

    // Produce message with JWT authentication
    JwtAuthenticatedProducer.produceMessage("Hello Secure Kafka! " + LocalDateTime.now(), jwtToken);

    // Validate token (for demonstration)
    try {
      Claims claims = JwtTokenGenerator.validateToken(jwtToken);
      System.out.println("Token validated for user: " + claims.getSubject());
      System.out.println("User roles: " + claims.get("roles"));
    } catch (Exception e) {
      System.err.println("Token validation failed: " + e.getMessage());
    }
  }
}
