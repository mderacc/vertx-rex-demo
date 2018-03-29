package com.ineat.demo.category.handlers;

import com.ineat.demo.common.client.DBClient;
import io.vavr.control.Option;
import io.vavr.control.Try;
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
public class FindCategoryHandler implements Handler<RoutingContext> {

  private Vertx vertx;
  private JsonObject config;

  @Override
  public void handle(RoutingContext routingContext){
    String categoryId = routingContext.request().params().get("id");
    List<String> fields = Option.of(routingContext.request().params().get("fields"))
        .map(str -> Arrays.asList(str.split(","))).getOrElse(new ArrayList<>());

    Consumer<JsonArray> consumer = category ->
        routingContext.response()
            .putHeader("content-type", "application/json")
            .end(Json.encode(category));

    Try.of(() -> Integer.parseInt(categoryId))
        .onSuccess(id -> {
          JsonObject query = new JsonObject().put("name", "id").put("value", categoryId);
          DBClient.consumeData(vertx, config.getString("entity"), query, fields, consumer);
        })
        .onFailure(ex -> routingContext.response()
            .putHeader("content-type", "text/html")
            .end( "Le type doit être une valeur numérique => " + ex.getMessage()));
  }
}
