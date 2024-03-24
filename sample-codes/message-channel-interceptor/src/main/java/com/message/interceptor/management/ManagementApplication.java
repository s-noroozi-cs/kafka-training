package com.message.interceptor.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.config.BindingServiceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.integration.config.GlobalChannelInterceptor;

@SpringBootApplication
public class ManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(ManagementApplication.class, args);
	}

  @Bean
  @GlobalChannelInterceptor(patterns = "*-in-*", order = Ordered.LOWEST_PRECEDENCE)
  public MyChannelInterceptor inputChannelInterceptor(BindingServiceProperties properties) {
    return new MyChannelInterceptor(properties, "input-channel[consumer]");
  }

  @Bean
  @GlobalChannelInterceptor(patterns = "*-out-*", order = Ordered.HIGHEST_PRECEDENCE)
  public MyChannelInterceptor outputChannelInterceptor(BindingServiceProperties properties) {
    return new MyChannelInterceptor(properties, "output-channel[producer]");
  }
}
