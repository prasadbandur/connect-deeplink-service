package com.goeuro.deeplink.providerconfig.wrapper;

import com.goeuro.deeplink.providerconfig.model.ProviderConfig;
import java.util.Optional;
import lombok.Getter;

public class ProviderConfigWrapper {

  private final ProviderConfig providerConfig;

  @Getter private final BookingConfigWrapper booking;

  public ProviderConfigWrapper(ProviderConfig providerConfig) {
    this.providerConfig = providerConfig;
    booking = new BookingConfigWrapper(providerConfig);
  }

  public String getProviderName() {
    return Optional.ofNullable(providerConfig).map(ProviderConfig::getName).orElse(null);
  }

  public Long getProviderDbId() {
    return Optional.ofNullable(providerConfig).map(ProviderConfig::getDbId).orElse(null);
  }
}
