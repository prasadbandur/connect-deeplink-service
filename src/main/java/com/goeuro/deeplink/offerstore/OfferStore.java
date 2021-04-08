package com.goeuro.deeplink.offerstore;

import com.goeuro.deeplink.utils.ReactiveUtil;
import com.goeuro.search2.pi.proto.OfferDetailsQuery;
import com.goeuro.search2.pi.proto.OfferDetailsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class OfferStore {

  private final ConnectOfferStoreGrpcClient storeGrpcClient;

  public Mono<OfferDetailsResponse> getOffer(OfferDetailsQuery query) {
    log.info(
        "Processing get offer for offerStoreId: {} and provider: {}",
        query.getOfferStoreId(),
        query.getProviderId());
    return storeGrpcClient
        .getOfferDetails(query)
        .onErrorResume(
            throwable ->
                ReactiveUtil.createMonoError(
                    throwable,
                    String.format(
                        "Error fetching offer with offerStoreId: %s and provider: %s",
                        query.getOfferStoreId(), query.getProviderId())))
        .switchIfEmpty(
            ReactiveUtil.createNotFoundMonoError(
                String.format(
                    "Offer not found with offerStoreId: %s and provider: %s",
                    query.getOfferStoreId(), query.getProviderId())));
  }
}
