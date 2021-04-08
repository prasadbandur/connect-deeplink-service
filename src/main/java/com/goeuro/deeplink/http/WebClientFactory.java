package com.goeuro.deeplink.http;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.goeuro.deeplink.config.AppConfig;
import com.goeuro.deeplink.http.model.WebClientConfiguration;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class WebClientFactory {

  private final AppConfig appConfig;

  public WebClient createWebClient(WebClientConfiguration webClientConfiguration) {
    WebClient.Builder builder =
        WebClient.builder()
            .baseUrl(webClientConfiguration.getHost())
            .exchangeStrategies(getExchangeStrategies(objectMapper()))
            .clientConnector(createReactorHttpConnector(webClientConfiguration));
    webClientConfiguration.getHeaders().forEach(builder::defaultHeader);
    return builder.build();
  }

  private ClientHttpConnector createReactorHttpConnector(
      WebClientConfiguration webClientConfiguration) {
    return new ReactorClientHttpConnector(
        createHttpClient(
            webClientConfiguration.getConnectTimeout(), webClientConfiguration.getReadTimeout()));
  }

  private HttpClient createHttpClient(int connectTimeout, int readTimeout) {
    return HttpClient.create()
        .tcpConfiguration(
            tcpClient ->
                tcpClient
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
                    .doOnConnected(
                        connection ->
                            connection.addHandlerLast(
                                new ReadTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS))));
  }

  private ObjectMapper objectMapper() {
    return Jackson2ObjectMapperBuilder.json()
        .modules(new JavaTimeModule())
        .serializationInclusion(JsonInclude.Include.NON_NULL)
        .featuresToDisable(
            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
            SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
            DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        .featuresToEnable(
            SerializationFeature.WRITE_ENUMS_USING_TO_STRING,
            DeserializationFeature.READ_ENUMS_USING_TO_STRING,
            MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
        .build();
  }

  private ExchangeStrategies getExchangeStrategies(ObjectMapper objectMapper) {
    return ExchangeStrategies.builder()
        .codecs(
            configurer -> {
              configurer.defaultCodecs().maxInMemorySize(appConfig.getMaxInMemorySize());
              configurer
                  .defaultCodecs()
                  .jackson2JsonEncoder(
                      new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
              configurer
                  .defaultCodecs()
                  .jackson2JsonDecoder(
                      new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
            })
        .build();
  }
}
