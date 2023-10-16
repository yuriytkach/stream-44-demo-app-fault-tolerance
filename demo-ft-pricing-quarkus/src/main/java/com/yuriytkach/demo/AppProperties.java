package com.yuriytkach.demo;

import java.time.Duration;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = "app")
public interface AppProperties {

  @WithDefault("PT0.2S")
  Duration defaultDelayMax();


  // Settings for calling product service

  @WithDefault("0")
  int productFailEach();

  @WithDefault("0")
  int productSleepMaxSec();

}
