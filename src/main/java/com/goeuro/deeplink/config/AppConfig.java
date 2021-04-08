package com.goeuro.deeplink.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Data
@Configuration
@EnableScheduling
@ConfigurationProperties
public class AppConfig {

  private int grpcPort;
  private int maxInMemorySize;
}
