package com.goeuro.deeplink.model;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class DeepLinkParameter {

  public static final String PARAM_PROVIDER = "provider";
  public static final String PARAM_REDIRECT_URL = "redirectUrl";
  public static final String PARAM_OUTBOUND_SOLUTION_ID = "outboundSolutionId";
  public static final String PARAM_OUTBOUND_OFFER_ID = "outboundOfferId";
  public static final String PARAM_OUTBOUND_SEAT_CLASS = "outboundSeatClass";
  public static final String PARAM_INBOUND_SOLUTION_ID = "inboundSolutionId";
  public static final String PARAM_INBOUND_OFFER_ID = "inboundOfferId";
  public static final String PARAM_INBOUND_SEAT_CLASS = "inboundSeatClass";
  public static final String PARAM_DEPARTURE_STATION_CODE = "departureStationCode";
  public static final String PARAM_ARRIVAL_STATION_CODE = "arrivalStationCode";
  public static final String PARAM_EXTERNAL_OFFER_REFERENCE = "externalOfferReference";
  public static final String PARAM_USER_DOMAIN = "userDomain";
  public static final String PARAM_AB_TEST_PARAMS = "abTestParameters";
}
