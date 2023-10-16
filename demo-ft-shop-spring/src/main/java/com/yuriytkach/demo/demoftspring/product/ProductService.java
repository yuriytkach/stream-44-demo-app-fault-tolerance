package com.yuriytkach.demo.demoftspring.product;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.github.javafaker.Faker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

  private final Faker faker;
  private final Map<String, FullProduct> products = new HashMap<>();

  public FullProduct getProductById(final String id) {
    return products.compute(
      id,
      (key, oldValue) -> oldValue == null
                         ? new FullProduct(id, faker.commerce().productName(), faker.random().nextInt(1, 5))
                         : new FullProduct(oldValue, faker.random().nextInt(1, 5))
    );
  }

  public record FullProduct(String id, String name, int rating) {
    FullProduct(FullProduct oldProduct, int newRating) {
      this(oldProduct.id, oldProduct.name, newRating);
    }
  }
}
