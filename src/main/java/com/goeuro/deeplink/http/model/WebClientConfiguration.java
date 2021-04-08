package com.goeuro.deeplink.http.model;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class WebClientConfiguration {

  private String host;
  private Map<String, String> headers;
  private int connectTimeout;
  private int readTimeout;
}
