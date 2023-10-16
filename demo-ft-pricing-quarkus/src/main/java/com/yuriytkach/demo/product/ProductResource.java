package com.yuriytkach.demo.product;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.yuriytkach.demo.AppProperties;

import jakarta.inject.Inject;
import jakarta.validation.constraints.NotEmpty;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("/pr")
public class ProductResource {

    @Inject
    @RestClient
    ProductApi productApi;

    @Inject
    AppProperties appProperties;

    @GET
    @Path("/{productId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Product findProduct(
      @PathParam("productId") @NotEmpty final String productId
    ) {
        return productApi.findProduct(
          productId,
          appProperties.productFailEach() > 0 ? appProperties.productFailEach() : null,
          appProperties.productSleepMaxSec() > 0 ? appProperties.productSleepMaxSec() : null
        );
    }

    public record Product(String id, String name, int rating, boolean fallback) { }
}
