package com.yuriytkach.demo.demoftspring;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ShopController {

  private final ShopService shopService;

  @GetMapping("/shop/{id}")
  public ShoppingProduct getProduct(@PathVariable final String id) {
    return shopService.getProduct(id);
  }

  public record ShoppingProduct(String id, String name, double price, boolean fallback) { }

}
