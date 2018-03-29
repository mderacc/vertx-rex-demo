package com.ineat.demo.price.handlers;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class CreatePriceHandler implements Handler<RoutingContext> {
  @Override
  public void handle(RoutingContext routingContext) {
    routingContext.response().end(routingContext.getBodyAsString());
  }
}
