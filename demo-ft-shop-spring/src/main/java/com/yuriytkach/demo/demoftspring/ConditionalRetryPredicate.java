package com.yuriytkach.demo.demoftspring;

import java.util.function.Predicate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConditionalRetryPredicate implements Predicate<ShopService.ProductPrice> {

  @Override
  public boolean test(final ShopService.ProductPrice productPrice) {
    final boolean zero = productPrice.price() <= 0.1;
    if (zero) {
      log.debug("Price is zero. Retry..");
    }
    return zero;
  }
}
