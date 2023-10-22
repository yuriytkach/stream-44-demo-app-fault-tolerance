package com.yuriytkach.demo.demoftspring;

import org.springframework.stereotype.Service;

import com.yuriytkach.demo.demoftspring.product.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopService {

  private final PriceService priceService;
  private final ProductService productService;

  public ShopController.ShoppingProduct getProduct(final String id) {
    final var productPrice = priceService.fetchPrice(id);
    final var productWithPrice = new ShopController.ShoppingProduct(
      id,
      productService.getProductById(id).name(),
      productPrice.price(),
      productPrice.fallback(),
      productPrice.cache()
    );
    log.info("{}", productWithPrice);
    return productWithPrice;
  }

  public record ProductPrice(String productId, double price, boolean fallback, boolean circuit, boolean cache) { }
}
