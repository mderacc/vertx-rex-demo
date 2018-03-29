package com.ineat.demo.price.handlers;

import com.ineat.demo.common.client.DBClient;
import io.vavr.control.Option;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FindPriceHandler implements Handler<RoutingContext> {

  private Vertx vertx;
  private JsonObject config;

  @Override
  public void handle(RoutingContext routingContext) {
    String productId = routingContext.request().params().get("productId");

    List<String> fields = Option.of(routingContext.request().params().get("fields"))
        .map(str -> Arrays.asList(str.split(","))).getOrElse(new ArrayList<>());

    Consumer<JsonArray> consumer = prices ->
        routingContext.response()
            .putHeader("content-type", "application/json")
            .end(Json.encode(prices));

    JsonObject query = new JsonObject().put("name", "id_product").put("value", productId);

    DBClient.consumeData(vertx, config.getString("entity"), query, fields, consumer);
  }
}
