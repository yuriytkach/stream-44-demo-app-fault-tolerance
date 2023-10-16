package com.yuriytkach.demo.product;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@RegisterRestClient(configKey = "product-api")
@Produces(MediaType.APPLICATION_JSON)
public interface ProductApi {

  @GET
  @Path("/product/{id}")
  ProductResource.Product findProduct(
    @PathParam("id") String id,
    @QueryParam("failEach") Integer failEach,
    @QueryParam("sleepMaxSec") Integer sleepMaxSec
  );
}
