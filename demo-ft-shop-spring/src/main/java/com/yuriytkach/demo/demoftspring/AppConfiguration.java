package com.yuriytkach.demo.demoftspring;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.github.javafaker.Faker;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.core.registry.EntryAddedEvent;
import io.github.resilience4j.core.registry.EntryRemovedEvent;
import io.github.resilience4j.core.registry.EntryReplacedEvent;
import io.github.resilience4j.core.registry.RegistryEventConsumer;
import io.github.resilience4j.retry.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableCaching
@RequiredArgsConstructor
@EnableConfigurationProperties({ RestProperties.class, AppProperties.class })
public class AppConfiguration {

  private final RestProperties restProperties;

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder
      .setConnectTimeout(restProperties.getConnectTimeout())
      .setReadTimeout(restProperties.getReadTimeout())
      .rootUri(restProperties.getPriceServiceUrl())
      .build();
  }

  @Bean
  public Faker faker() {
    return new Faker();
  }

  @Bean
  public RegistryEventConsumer<Retry> myRetryRegistryEventConsumer() {

    return new RegistryEventConsumer<>() {
      @Override
      public void onEntryAddedEvent(EntryAddedEvent<Retry> entryAddedEvent) {
        entryAddedEvent.getAddedEntry().getEventPublisher().onEvent(event -> log.info(event.toString()));
      }

      @Override
      public void onEntryRemovedEvent(EntryRemovedEvent<Retry> entryRemoveEvent) {

      }

      @Override
      public void onEntryReplacedEvent(EntryReplacedEvent<Retry> entryReplacedEvent) {

      }
    };
  }

  @Bean
  public RegistryEventConsumer<CircuitBreaker> myRegistryEventConsumer() {

    return new RegistryEventConsumer<>() {
      @Override
      public void onEntryAddedEvent(EntryAddedEvent<CircuitBreaker> entryAddedEvent) {
        entryAddedEvent.getAddedEntry().getEventPublisher().onEvent(event -> log.info(event.toString()));
      }

      @Override
      public void onEntryRemovedEvent(EntryRemovedEvent<CircuitBreaker> entryRemoveEvent) {

      }

      @Override
      public void onEntryReplacedEvent(EntryReplacedEvent<CircuitBreaker> entryReplacedEvent) {

      }
    };
  }

}
