package com.goeuro.deeplink.providerconfig.wrapper;

import static com.goeuro.deeplink.providerconfig.wrapper.CarriersConfigWrapper.getCarrierConfig;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;

import com.goeuro.deeplink.providerconfig.model.BookingConfig;
import com.goeuro.deeplink.providerconfig.model.CarrierConfig;
import com.goeuro.deeplink.providerconfig.model.ProviderConfig;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BookingConfigWrapper {

  private final ProviderConfig providerConfig;

  public boolean isOnSite() {
    return Optional.ofNullable(providerConfig)
        .map(ProviderConfig::getBooking)
        .map(BookingConfig::getOnSite)
        .orElse(false);
  }

  public boolean isOnSite(String carrierCode) {
    return getCarrierConfig(providerConfig, carrierCode)
        .map(CarrierConfig::getBooking)
        .map(BookingConfig::getOnSite)
        .orElse(isOnSite());
  }

  public boolean isOnSite(List<String> carrierCodes) {
    return isEmpty(carrierCodes) ? isOnSite() : carrierCodes.stream().allMatch(this::isOnSite);
  }

  // TODO
  //  public boolean isOnSite(Journey journey) {
  //    return isOnSite(extractCarrierCodes(journey));
  //  }
}
