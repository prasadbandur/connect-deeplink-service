package com.goeuro.deeplink.config;

import com.google.common.cache.CacheBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.guava.GuavaCache;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class CacheFactory {

  private static final String DEFAULT_CACHE_NAME = "connect-search-cache";

  public GuavaCache getCache(Duration ttl, Long maximumSize) {
    return new GuavaCache(
        DEFAULT_CACHE_NAME,
        CacheBuilder.newBuilder().maximumSize(maximumSize).expireAfterWrite(ttl).build());
  }
}
