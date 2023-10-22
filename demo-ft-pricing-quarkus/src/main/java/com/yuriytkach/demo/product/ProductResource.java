package com.yuriytkach.demo.product;

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
    ProductService productService;

    @GET
    @Path("/{productId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Product findProduct(
      @PathParam("productId") @NotEmpty final String productId
    ) {
        return productService.findProduct(productId);
    }

    public record Product(String id, String name, int rating, boolean fallback, boolean cache) { }
}
