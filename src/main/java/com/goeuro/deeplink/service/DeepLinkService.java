package com.goeuro.deeplink.service;

import com.goeuro.deeplink.model.DeepLinkParameter;
import com.goeuro.deeplink.offerstore.OfferStore;
import com.goeuro.deeplink.providerconfig.service.ProviderConfigService;
import com.goeuro.deeplink.utils.ReactiveUtil;
import com.goeuro.coverage.offer.store.protobuf.BookingOffer;
import com.goeuro.coverage.offer.store.protobuf.OfferStoreSegment;
import com.goeuro.search2.model.proto.Currency;
import com.goeuro.search2.model.proto.Deeplink;
import com.goeuro.search2.pi.proto.OfferDetailsQuery;
import com.goeuro.search2.pi.proto.OfferDetailsResponse;
import com.goeuro.search2.pi.proto.PiboxDeeplinkQuery;
import com.google.protobuf.util.JsonFormat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeepLinkService {

  private static final String BOOKING_PROVIDER_PREFIX = "goeuroBooking";
  private static final JsonFormat.Parser JSON_PARSER = JsonFormat.parser();

  private final OfferStore offerStore;
  private final ProviderConfigService configService;

  public Mono<Deeplink> getDeepLink(PiboxDeeplinkQuery query) {
    log.info("Fetching offerDetails for offerStoreId: {}", query.getOfferStoreId());
    var request =
        OfferDetailsQuery.newBuilder()
            .setOfferStoreId(query.getOfferStoreId())
            .setLocale(query.getLocale())
            .setProviderId(query.getProviderKey())
            .build();
    return offerStore
        .getOffer(request)
        .flatMap(response -> buildDeepLink(response, query.getOfferStoreId()))
        .onErrorResume(
            throwable ->
                ReactiveUtil.createMonoError(
                    throwable,
                    String.format(
                        "Error while building deepLink for offerStoreId: %s and provider: %s",
                        query.getOfferStoreId(), query.getProviderKey())));
  }

  private Mono<Deeplink> buildDeepLink(OfferDetailsResponse response, String offerStoreId) {
    log.info("Building DeepLink for offerStoreId: {}", offerStoreId);
    return Mono.fromCallable(
            () -> {
              var offerBuilder = BookingOffer.newBuilder();
              JSON_PARSER.ignoringUnknownFields().merge(response.getMessage(), offerBuilder);
              return offerBuilder.build();
            })
        .map(this::createDeepLink)
        .doOnError(
            throwable ->
                log.error(
                    "Error parsing OfferDetailsResponse json for offerStoreId : {}",
                    offerStoreId,
                    throwable))
        .onErrorResume(Mono::error);
  }

  private Deeplink createDeepLink(BookingOffer bookingOffer) {
    var bookingProviderName =
        configService
                .getProviderConfigWrapper(bookingOffer.getProvider())
                .getBooking()
                .isOnSite(getCarriers(bookingOffer))
            ? BOOKING_PROVIDER_PREFIX + StringUtils.capitalize(bookingOffer.getProvider())
            : bookingOffer.getProvider();
    return Deeplink.newBuilder()
        .setServiceProvider(bookingProviderName)
        .setCurrency(Currency.valueOf(bookingOffer.getUserCurrency()))
        .setUrl(bookingOffer.getProviderParamsMap().get(DeepLinkParameter.PARAM_REDIRECT_URL))
        .setMethod(HttpMethod.GET.name())
        .build();
  }

  private List<String> getCarriers(BookingOffer bookingOffer) {
    return Stream.of(bookingOffer.getOutboundSegment(), bookingOffer.getInboundSegment())
        .filter(Objects::nonNull)
        .map(OfferStoreSegment::getCarrierCode)
        .distinct()
        .collect(Collectors.toList());
  }
}
