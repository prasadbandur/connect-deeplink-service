package com.goeuro.deeplink.providerconfig.model;

public class MissingProviderConfigException extends RuntimeException {

  private static final String FATAL_ERROR_MSG =
      "[FATAL ERROR] An unexpected error occurred to fetch external provider config values. "
          + "The application cannot be resumed if the provider configurations are not properly loaded.";

  public MissingProviderConfigException() {
    super(FATAL_ERROR_MSG);
  }

  public MissingProviderConfigException(Throwable throwable) {
    super(FATAL_ERROR_MSG, throwable);
  }
}
