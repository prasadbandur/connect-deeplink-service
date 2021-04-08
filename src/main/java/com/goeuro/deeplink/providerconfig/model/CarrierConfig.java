package com.goeuro.deeplink.providerconfig.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarrierConfig {

  private String name;

  private BookingConfig booking = new BookingConfig();
}
