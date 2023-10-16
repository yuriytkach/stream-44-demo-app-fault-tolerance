package com.yuriytkach.demo.demoftspring;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PriceService {

  private final RestTemplate restTemplate;
  private final AppProperties appProperties;

  private final CircuitBreakerRegistry circuitBreakerRegistry;


  @Retry(name = "pricingService", fallbackMethod = "fetchPriceFallback")
  @CircuitBreaker(name = "pricingService")
  public ShopService.ProductPrice fetchPrice(final String id) {
    log.debug(
      ">> Fetching price for product {}, failEach: {}, maxSleep: {}",
      id,
      appProperties.getPriceFailEach(),
      appProperties.getPriceSleepMaxSec()
    );

    final String request = buildRequestUrlPath(id);

    final ShopService.ProductPrice price = restTemplate.getForObject(
      request,
      ShopService.ProductPrice.class
    );
    log.info("<< Fetched price: {}", price);
    return price;
  }

  private String buildRequestUrlPath(final String id) {
    final StringBuilder requestBuilder = new StringBuilder("/price/").append(id);
    if (appProperties.getPriceFailEach() > 0) {
      requestBuilder.append("?failEach=").append(appProperties.getPriceFailEach());
    }
    if (appProperties.getPriceSleepMaxSec() > 0) {
      requestBuilder
        .append(requestBuilder.indexOf("?") > 0 ? "&" : "?" )
        .append("sleepMaxSec=").append(appProperties.getPriceSleepMaxSec());
    }
    return requestBuilder.toString();
  }

  public ShopService.ProductPrice fetchPriceFallback(final String id, final Exception ex) {
    final io.github.resilience4j.circuitbreaker.CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker(
      "pricingService");

    log.warn(
      "Fallback for product price {} (CB: {}) because of exception {}",
      id,
      cb.getState(),
      ex.getMessage()
    );
    return new ShopService.ProductPrice(id, 1.0, true,
      cb.getState() != io.github.resilience4j.circuitbreaker.CircuitBreaker.State.CLOSED);
  }

}
