package com.ineat.demo.price;

import com.ineat.demo.common.MainApiVerticle;
import com.ineat.demo.price.handlers.CreatePriceHandler;
import com.ineat.demo.price.handlers.FindPriceHandler;
import io.vertx.ext.web.handler.BodyHandler;

public class MainPriceVerticle extends MainApiVerticle {

  @Override
  public void start() throws Exception {
    super.start();
    getConfigRetriever(vertx).getConfig( event -> {
      event.result().forEach(entry -> config().put(entry.getKey(), entry.getValue()));
      router.route("/price*").handler(BodyHandler.create());
      router.get("/price").handler(new FindPriceHandler(vertx, config()));
      router.post("/price").handler(new CreatePriceHandler());

      vertx.createHttpServer()
          .requestHandler(router::accept)
          .listen(config().getInteger("PRICE_PORT"));
    });
  }
}
