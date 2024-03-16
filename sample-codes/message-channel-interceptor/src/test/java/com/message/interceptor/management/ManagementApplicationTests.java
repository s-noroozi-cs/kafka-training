package com.message.interceptor.management;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ManagementApplicationTests {

  @LocalServerPort private int port;

  @SneakyThrows
  private boolean produce_new_message() {
    HttpResponse response =
        HttpClient.newBuilder()
            .build()
            .send(
                HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:%d/messages/uuid".formatted(port)))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build(),
                HttpResponse.BodyHandlers.discarding());
    return response.statusCode() == HttpStatus.ACCEPTED.value();
  }

  @Test
  @SneakyThrows
  void produce_message() {
    Assertions.assertTrue(produce_new_message());
    Thread.sleep(10_000L);
  }
}
