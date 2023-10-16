package com.yuriytkach.demo;

import jakarta.inject.Inject;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/price")
public class PriceResource {

    @Inject
    PricingService pricingService;

    @GET
    @Path("/{productId}")
    @Produces(MediaType.APPLICATION_JSON)
    public ProductPrice price(
      @PathParam("productId") @NotEmpty final String productId,
      @QueryParam("failEach") @Min(1) @Positive final Integer failEachRequest,
      @QueryParam("sleepMaxSec") @Min(1) @Positive final Integer sleepMaxSec
    ) {
        return pricingService.calculatePrice(productId, failEachRequest, sleepMaxSec);
    }

    public record ProductPrice(String productId, double price) { }
}
