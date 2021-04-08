package com.goeuro.deeplink.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "provider-config")
public class ProviderConfigEnvironment {

  private String host;
  private int connectionTimeout;
  private int readTimeout;
}
