package com.yuriytkach.demo.demoftspring;

import static com.yuriytkach.demo.demoftspring.PriceService.PRICES_CACHE;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.maciejwalkowiak.wiremock.spring.ConfigureWireMock;
import com.maciejwalkowiak.wiremock.spring.EnableWireMock;
import com.maciejwalkowiak.wiremock.spring.InjectWireMock;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableWireMock({
  @ConfigureWireMock(name = "pricing-service", property = "rest.price-service-url")
})
@TestPropertySource(properties = {
  "rest.read-timeout = PT1S"
})
class DemoFtSpringApplicationTests {

  @InjectWireMock("pricing-service")
  private WireMockServer wiremock;

  @Autowired
  private Environment env;

  @Autowired
  private CacheManager cacheManager;

  @Autowired
  private PriceService tested;

  @Test
  void priceReturnsCorrectly() {
    System.out.println("PriceService mock: " + env.getProperty("rest.price-service-url"));

    wiremock.stubFor(
      WireMock.get(WireMock.urlPathEqualTo("/price/1"))
        .willReturn(WireMock.aResponse()
          .withStatus(200)
          .withHeader("Content-Type", "application/json")
          .withBody("""
            			{ "productId": "1", "price": 56.0 }
            """)
        )
    );

    final ShopService.ProductPrice actual = tested.fetchPrice("1");

    assertThat(actual).isEqualTo(new ShopService.ProductPrice("1", 56.0, false, false, false));

    wiremock.verify(1, WireMock.getRequestedFor(WireMock.urlPathEqualTo("/price/1")));

    final ShopService.ProductPrice inCache = cacheManager.getCache(PRICES_CACHE).get(
      "1",
      ShopService.ProductPrice.class
    );
    assertThat(inCache).isEqualTo(new ShopService.ProductPrice("1", 56.0, false, false, false));
  }

  @Test
  void priceRetryOnFailure() {
    System.out.println("PriceService mock: " + env.getProperty("rest.price-service-url"));

    wiremock.stubFor(
      WireMock.get(WireMock.urlPathEqualTo("/price/1")).inScenario("fail-scenario")
        .willReturn(WireMock.aResponse()
          .withStatus(500)
          .withHeader("Content-Type", "application/json")
        )
        .willSetStateTo("failed")
    );

    wiremock.stubFor(
      WireMock.get(WireMock.urlPathEqualTo("/price/1")).inScenario("fail-scenario")
        .whenScenarioStateIs("failed")
        .willReturn(WireMock.aResponse()
          .withStatus(200)
          .withHeader("Content-Type", "application/json")
          .withBody("""
            			{ "productId": "1", "price": 56.0 }
            """)
        )
    );

    final ShopService.ProductPrice actual = tested.fetchPrice("1");

    assertThat(actual).isEqualTo(new ShopService.ProductPrice("1", 56.0, false, false, false));

    wiremock.verify(2, WireMock.getRequestedFor(WireMock.urlPathEqualTo("/price/1")));
  }

  @Test
  void priceRetryOnTimeout() {
    System.out.println("PriceService mock: " + env.getProperty("rest.price-service-url"));

    wiremock.stubFor(
      WireMock.get(WireMock.urlPathEqualTo("/price/1")).inScenario("fail-scenario")
        .willReturn(WireMock.aResponse()
          .withStatus(200)
          .withHeader("Content-Type", "application/json")
          .withFixedDelay(1_500)
          .withBody("""
            			{ "productId": "1", "price": 123.0 }
            """)
        )
        .willSetStateTo("failed")
    );

    wiremock.stubFor(
      WireMock.get(WireMock.urlPathEqualTo("/price/1")).inScenario("fail-scenario")
        .whenScenarioStateIs("failed")
        .willReturn(WireMock.aResponse()
          .withStatus(200)
          .withHeader("Content-Type", "application/json")
          .withBody("""
            			{ "productId": "1", "price": 56.0 }
            """)
        )
    );

    final ShopService.ProductPrice actual = tested.fetchPrice("1");

    assertThat(actual).isEqualTo(new ShopService.ProductPrice("1", 56.0, false, false, false));

    wiremock.verify(2, WireMock.getRequestedFor(WireMock.urlPathEqualTo("/price/1")));
  }

  @Test
  void priceFallbackOnFailure() {
    System.out.println("PriceService mock: " + env.getProperty("rest.price-service-url"));

    wiremock.stubFor(
      WireMock.get(WireMock.urlPathEqualTo("/price/1")).inScenario("fail-scenario")
        .willReturn(WireMock.aResponse()
          .withStatus(200)
          .withHeader("Content-Type", "application/json")
          .withBody("""
            			{ "productId": "1", "price": 56.0 }
            """)
        )
        .willSetStateTo("ok")
    );

    wiremock.stubFor(
      WireMock.get(WireMock.urlPathEqualTo("/price/1")).inScenario("fail-scenario")
        .whenScenarioStateIs("ok")
        .willReturn(WireMock.aResponse()
          .withStatus(500)
          .withHeader("Content-Type", "application/json")
        )
    );

    final ShopService.ProductPrice actual = tested.fetchPrice("1");
    assertThat(actual).isEqualTo(new ShopService.ProductPrice("1", 56.0, false, false, false));

    final ShopService.ProductPrice actual2 = tested.fetchPrice("1");
    assertThat(actual2).isEqualTo(new ShopService.ProductPrice("1", 56.0, true, false, true));
    wiremock.verify(3, WireMock.getRequestedFor(WireMock.urlPathEqualTo("/price/1")));
  }

}
