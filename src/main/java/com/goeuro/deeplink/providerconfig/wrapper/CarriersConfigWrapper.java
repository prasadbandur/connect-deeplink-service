package com.goeuro.deeplink.providerconfig.wrapper;

import com.goeuro.deeplink.providerconfig.model.CarrierConfig;
import com.goeuro.deeplink.providerconfig.model.ProviderConfig;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;

@RequiredArgsConstructor
public class CarriersConfigWrapper {

  private final ProviderConfig providerConfig;

  private static Optional<List<CarrierConfig>> getNotEmptyCarrierConfigs(
      ProviderConfig providerConfig) {
    return Optional.ofNullable(providerConfig)
        .map(ProviderConfig::getCarriers)
        .filter(ObjectUtils::isNotEmpty);
  }

  static Optional<CarrierConfig> getCarrierConfig(
      ProviderConfig providerConfig, String carrierCode) {
    return getNotEmptyCarrierConfigs(providerConfig)
        .flatMap(
            carriers ->
                carriers.stream()
                    .filter(carrier -> carrier.getName().equals(carrierCode))
                    .findFirst());
  }

  // TODO
  //  public boolean areAllEnabled(Journey journey) {
  //    return extractCarrierCodes(journey).stream().noneMatch(this::isDisabled);
  //  }

  //  static List<String> extractCarrierCodes(Journey journey) {
  //    return journey.getLegs().stream()
  //        .flatMap(leg -> leg.getSegments().stream())
  //        .map(JourneySegment::getCarrierCode)
  //        .filter(Objects::nonNull)
  //        .distinct()
  //        .collect(Collectors.toList());
  //  }
}
