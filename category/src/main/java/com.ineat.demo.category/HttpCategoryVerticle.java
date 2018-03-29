package com.ineat.demo.category;

import com.ineat.demo.category.handlers.FindCategoryHandler;
import com.ineat.demo.common.MainApiVerticle;

public class HttpCategoryVerticle extends MainApiVerticle {

  @Override
  public void start() throws Exception {
    super.start();
    getConfigRetriever(vertx).getConfig( event -> {
      event.result().forEach(entry -> config().put(entry.getKey(), entry.getValue()));
      router.get("/category/:id").handler(new FindCategoryHandler(vertx, config()));

      vertx.createHttpServer()
          .requestHandler(router::accept)
          .listen(config().getInteger("CATEGORY_PORT"));
    });
  }
}
