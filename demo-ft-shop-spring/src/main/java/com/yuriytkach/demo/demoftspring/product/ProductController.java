package com.yuriytkach.demo.demoftspring.product;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yuriytkach.demo.demoftspring.AppProperties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;
  private final AppProperties appProperties;

  private final AtomicInteger requestCounter = new AtomicInteger(0);
  private final SecureRandom sleepRandom = new SecureRandom();

  @GetMapping("/product/{id}")
  public ProductService.FullProduct getProduct(
    @PathVariable @NotEmpty final String id,
    @RequestParam(value = "failEach", required = false) @Min(1) @Positive final Integer failEachRequest,
    @RequestParam(value = "sleepMaxSec", required = false) @Min(1) @Positive final Integer sleepMaxSec
  ) {
    log.info(">> Search product: {}, failEach: {}, Sleep: {}...", id, failEachRequest, sleepMaxSec);

    if (failEachRequest != null && requestCounter.incrementAndGet() % failEachRequest == 0) {
      log.info("Failing request for product {}", id);
      throw new RuntimeException("Failed to return product");
    }

    final var product = productService.getProductById(id);

    simulateDelay(sleepMaxSec);
    log.info("<< {}", product);
    return product;
  }

  @SneakyThrows
  private void simulateDelay(final Integer sleepMaxSec) {
    final long sleepMilli;
    if (sleepMaxSec == null) {
      sleepMilli = sleepRandom.nextLong(appProperties.getDefaultDelayMax().toMillis());
    } else {
      sleepMilli = sleepRandom.nextLong(Duration.ofSeconds(sleepMaxSec).toMillis());
    }
    log.debug("Sleeping {} ms", sleepMilli);
    Thread.sleep(sleepMilli);
  }
}
