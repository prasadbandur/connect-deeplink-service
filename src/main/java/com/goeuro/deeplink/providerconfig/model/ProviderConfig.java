package com.goeuro.deeplink.providerconfig.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Read the documentation of the schema of provider configurations here:
 * https://github.com/goeuro/provider-config/blob/master/graphql/schema.md
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderConfig {

  private String name;

  private BookingConfig booking = new BookingConfig();

  private List<CarrierConfig> carriers = new ArrayList<>();

  private Long dbId;

  @JsonProperty("provider")
  public void setProvider(Map<String, String> provider) {
    this.name = provider.get("name");
    this.dbId = Long.parseLong(provider.get("dbId"));
  }
}
