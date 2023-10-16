package com.yuriytkach.demo.demoftspring;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
@ConfigurationProperties(prefix = "app")
public class AppProperties {

  private final Duration defaultDelayMax;

  @PositiveOrZero
  private final int priceFailEach;

  @PositiveOrZero
  private final int priceSleepMaxSec;

}
