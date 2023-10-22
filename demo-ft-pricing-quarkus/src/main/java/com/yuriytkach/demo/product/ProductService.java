package com.yuriytkach.demo.product;

import java.util.concurrent.CompletableFuture;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.yuriytkach.demo.AppProperties;

import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheName;
import io.quarkus.cache.CaffeineCache;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
class ProductService {

  @Inject
  @RestClient
  ProductApi productApi;

  @Inject
  AppProperties appProperties;

  @CacheName("product-cache")
  Cache cache;

  @Retry(
    maxRetries = 2,
    retryOn = { WebApplicationException.class, ProcessingException.class },
    delay = 100,
    jitter = 10,
    maxDuration = 10_000,
    abortOn = NullPointerException.class
  )
  @Fallback(fallbackMethod = "fallbackFindProduct")
  @CircuitBreaker(
    requestVolumeThreshold = 10,
    failureRatio = 0.4,
    delay = 3_000,
    successThreshold = 3
  )
  ProductResource.Product findProduct(String id) {
    final var product = productApi.findProduct(
      id,
      appProperties.productFailEach() > 0 ? appProperties.productFailEach() : null,
      appProperties.productSleepMaxSec() > 0 ? appProperties.productSleepMaxSec() : null
    );

    cache.as(CaffeineCache.class).put(id, CompletableFuture.completedFuture(product));

    return product;
  }

  ProductResource.Product fallbackFindProduct(
    final String id,
    final Exception cause
  ) {
    log.warn("Fallback for product {} because {}: {}",
      id, cause.getClass().getSimpleName(), cause.getMessage());

    final ProductResource.Product cached = cache
      .get(id, k -> (ProductResource.Product) null)
      .await().indefinitely();

    return new ProductResource.Product(
      id,
      cached == null ? "null" : cached.name(),
      cached == null ? 0 : cached.rating(),
      true,
      cached != null
    );
  }
}
