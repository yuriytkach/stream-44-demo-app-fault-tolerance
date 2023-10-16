package com.yuriytkach.demo.demoftspring;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "rest")
public class RestProperties {

  private final Duration readTimeout;
  private final Duration connectTimeout;

  private final String priceServiceUrl;

}
