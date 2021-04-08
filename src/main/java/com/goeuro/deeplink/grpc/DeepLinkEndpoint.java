package com.goeuro.deeplink.grpc;

import com.goeuro.deeplink.service.DeepLinkService;
import com.goeuro.deeplink.utils.ReactiveUtil;
import com.goeuro.search2.model.proto.Deeplink;
import com.goeuro.search2.pi.proto.PiboxDeeplinkQuery;
import com.goeuro.search2.pi.proto.PiboxDeeplinkResponse;
import com.goeuro.search2.pi.proto.PiboxDeeplinkServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeepLinkEndpoint extends PiboxDeeplinkServiceGrpc.PiboxDeeplinkServiceImplBase {

  private final DeepLinkService deepLinkService;

  @Override
  public void createDeeplink(
      PiboxDeeplinkQuery query, StreamObserver<PiboxDeeplinkResponse> responseObserver) {
    var offerStoreId = query.getOfferStoreId();
    log.info("Started to call [createDeepLink] for offerStoreId id [{}]", offerStoreId);
    deepLinkService
        .getDeepLink(query)
        .map(this::buildPiboxDeepLinkResponse)
        .doOnSuccess(deepLink -> logSuccess(deepLink, offerStoreId))
        .switchIfEmpty(createNotfoundError(query))
        .doOnError(throwable -> logError(throwable, query))
        .onErrorResume(throwable -> createMonoError(throwable, query))
        .subscribe(new GrpcCustomSubscriber<>(responseObserver));
  }

  private PiboxDeeplinkResponse buildPiboxDeepLinkResponse(Deeplink deepLink) {
    return PiboxDeeplinkResponse.newBuilder().setDeeplink(deepLink).build();
  }

  private Mono<PiboxDeeplinkResponse> createNotfoundError(PiboxDeeplinkQuery query) {
    return ReactiveUtil.createNotFoundMonoError(
        String.format("OfferDetails not found for offerStoreId %s", query.getOfferStoreId()));
  }

  private Mono<PiboxDeeplinkResponse> createMonoError(
      Throwable throwable, PiboxDeeplinkQuery query) {
    return ReactiveUtil.createMonoError(
        throwable,
        String.format(
            "Error while creating DeepLink for offerStoreId: %s and provider: %s",
            query.getOfferStoreId(), query.getProviderKey()));
  }

  private void logError(Throwable throwable, PiboxDeeplinkQuery query) {
    log.error(
        "Error while getting offer from store with id = [{}] and provider = [{}]  ",
        query.getOfferStoreId(),
        query.getProviderKey(),
        throwable);
  }

  private void logSuccess(PiboxDeeplinkResponse deepLink, String offerStoreId) {
    log.info(
        "DeepLink {} successfully returned for OfferStoreId: {} and provider: {}",
        deepLink.getDeeplink(),
        offerStoreId,
        deepLink.getDeeplink().getServiceProvider());
  }
}
