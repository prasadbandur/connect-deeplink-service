package com.goeuro.deeplink.utils;

import io.grpc.Status;
import lombok.experimental.UtilityClass;
import reactor.core.publisher.Mono;

@UtilityClass
public class ReactiveUtil {

  public <T> Mono<T> createMonoError(Throwable throwable, String message) {
    return Mono.error(
        Status.INTERNAL.augmentDescription(message).withCause(throwable).asException());
  }

  public static <T> Mono<T> createNotFoundMonoError(String message) {
    return Mono.error(Status.NOT_FOUND.augmentDescription(message).asException());
  }
}
