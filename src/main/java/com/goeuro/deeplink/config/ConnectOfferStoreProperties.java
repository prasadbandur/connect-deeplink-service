package com.goeuro.deeplink.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "connect.offer")
public class ConnectOfferStoreProperties {

  private String host;
  private Integer port;
}
