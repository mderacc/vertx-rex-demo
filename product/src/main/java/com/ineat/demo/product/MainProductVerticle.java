package com.ineat.demo.product;

import com.ineat.demo.common.MainApiVerticle;
import com.ineat.demo.product.handlers.FindProductHandler;

public class MainProductVerticle extends MainApiVerticle {

  @Override
  public void start() throws Exception {
    super.start();
    getConfigRetriever(vertx).getConfig( event -> {
      event.result().forEach(entry -> config().put(entry.getKey(), entry.getValue()));
      router.get("/product/:id").handler(new FindProductHandler(vertx, config()));
      vertx.createHttpServer()
          .requestHandler(router::accept)
          .listen(config().getInteger("PRODUCT_PORT"));
    });
  }
}
