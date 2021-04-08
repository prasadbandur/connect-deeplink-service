package com.goeuro.deeplink.providerconfig.service;

import static java.time.temporal.ChronoUnit.DAYS;

import com.goeuro.deeplink.config.CacheFactory;
import com.goeuro.deeplink.providerconfig.client.ProviderConfigClient;
import com.goeuro.deeplink.providerconfig.model.ProviderConfig;
import com.goeuro.deeplink.providerconfig.wrapper.ProviderConfigWrapper;
import java.time.Duration;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.VisibleForTesting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.guava.GuavaCache;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
public class ProviderConfigService {

  private static final int CONFIG_RELOAD_INTERVAL = 2 * 60 * 1000; // 2 minutes config reload
  private static final Duration EXPIRY_AFTER_WRITE_DURATION =
      Duration.of(1, DAYS); // 1 day of expiry TTL
  private static final long MAX_CACHE_SIZE = 1000; // maximum providers

  private final ProviderConfigClient providerConfigClient;
  private final GuavaCache cache;

  @Autowired
  public ProviderConfigService(
      ProviderConfigClient providerConfigClient, CacheFactory cacheFactory) {
    this.providerConfigClient = providerConfigClient;
    this.cache = cacheFactory.getCache(EXPIRY_AFTER_WRITE_DURATION, MAX_CACHE_SIZE);
    loadCache();
  }

  // for the scheduler to periodically eagerly refresh the cache
  @VisibleForTesting
  @Scheduled(fixedRate = CONFIG_RELOAD_INTERVAL, initialDelay = CONFIG_RELOAD_INTERVAL)
  void refreshCache() {
    try {
      loadCache();
    } catch (Exception exception) {
      log.warn("Error occurred while refreshing cache", exception);
    }
  }

  private void loadCache() {
    getProviderConfigMapFromAPI()
        .subscribeOn(Schedulers.immediate())
        .subscribe(
            successConsumer -> successConsumer.forEach(cache::put),
            null,
            () -> log.info("Successfully Loaded cache " + cache));
  }

  public ProviderConfigWrapper getProviderConfigWrapper(String providerName) {
    var providerConfig = getProviderConfig(providerName);
    return new ProviderConfigWrapper(providerConfig);
  }

  private ProviderConfig getProviderConfig(String providerName) {
    Objects.requireNonNull(providerName, "provider name cannot be null");
    log.debug("Getting provider config from cache for provider {}", providerName);
    return cache.get(providerName, ProviderConfig.class);
  }

  private Mono<Map<String, ProviderConfig>> getProviderConfigMapFromAPI() {
    return providerConfigClient
        .getAll()
        .retry(3)
        .map(
            successConsumer ->
                successConsumer.stream()
                    .map(providerConfig -> Map.entry(providerConfig.getName(), providerConfig))
                    .collect(Collectors.toMap(Entry::getKey, Entry::getValue)));
  }
}
