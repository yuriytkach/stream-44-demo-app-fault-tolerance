package com.yuriytkach.demo;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class PricingService {

  @Inject
  AppProperties appProperties;

  private final AtomicInteger requestCounter = new AtomicInteger(0);
  private final SecureRandom sleepRandom = new SecureRandom();

  public PriceResource.ProductPrice calculatePrice(
    final String productId,
    final Integer failEachRequest,
    final Integer sleepMaxSec
  ) {
    if (sleepRandom.nextInt() % 3 == 0) {
      log.info("Returning zero price for product {}", productId);
      return new PriceResource.ProductPrice(productId, 0);
    }

    if (failEachRequest != null && requestCounter.incrementAndGet() % failEachRequest == 0) {
      log.info("Failing request for product {}", productId);
      throw new RuntimeException("Failed to calculate price");
    }

    final var price = computePrice(productId);

    simulateDelay(sleepMaxSec);

    return new PriceResource.ProductPrice(productId, price);
  }

  private double computePrice(final String productId) {
    final var hash = productId.hashCode();
    final var jitter = Math.random() / 4 + 1;
    final var adjustedPrice = Math.abs(hash) * jitter;
    final var price = Math.ceil(adjustedPrice);
    log.info("Computed price {} for product {}", price, productId);
    return price;
  }

  @SneakyThrows
  private void simulateDelay(final Integer sleepMaxSec) {
    final long sleepMilli;
    if (sleepMaxSec == null) {
      sleepMilli = sleepRandom.nextLong(appProperties.defaultDelayMax().toMillis());
    } else {
      sleepMilli = sleepRandom.nextLong(Duration.ofSeconds(sleepMaxSec).toMillis());
    }

    log.debug("Sleeping for {} ms", sleepMilli);
    Thread.sleep(sleepMilli);
  }
}
