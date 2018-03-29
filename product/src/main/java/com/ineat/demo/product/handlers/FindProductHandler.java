package com.ineat.demo.product.handlers;

import com.ineat.demo.common.client.DBClient;
import io.vavr.control.Option;
import io.vavr.control.Try;
import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import java.util.ArrayList;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FindProductHandler implements Handler<RoutingContext> {

  private Vertx vertx;
  private JsonObject config;

  @Override
  public void handle(RoutingContext routingContext) {
    String productId = routingContext.request().params().get("id");

    Consumer<JsonArray> consumer = products -> {
      JsonObject productObj = getFormattedProduct(products.getJsonObject(0));
      String categoryId = products.getJsonObject(0).getString("id_category");

      // Récupération de la category via l'event bus
      vertx.eventBus().send("get-product-category", categoryId, reply -> {
        if (reply.succeeded()) {
          Option.of(reply).peek(category ->
              productObj.put("category", category.result().body()));
        }

        // Chargement des prix du produit
        CircuitBreaker breaker = CircuitBreaker.create("circuit-breaker", vertx,
            new CircuitBreakerOptions()
                .setMaxFailures(5)
                .setTimeout(2000)
        );
        breaker.execute(future ->
            fillProductWithPrices(productId, prices ->
                routingContext.response()
                    .putHeader("content-type", "text/html")
                    .end(Json.encode(productObj.put("prices", prices)))
            )
        ).setHandler(ar -> {
              System.out.println("Impossible de contacter l'api-prices.");
              routingContext.response()
                  .putHeader("content-type", "text/html")
                  .end(Json.encode(productObj));
            }
        );
      });
    };

    Try.of(() -> Integer.parseInt(productId))
        .onSuccess(id -> {
          JsonObject query = new JsonObject().put("name", "id").put("value", productId);
          DBClient.consumeData(vertx, config.getString("entity"), query, new ArrayList<>(), consumer);
        })
        .onFailure(ex -> routingContext.response()
            .putHeader("content-type", "text/html")
            .end("Bad type => " + ex.getMessage()));
  }

  private JsonObject getFormattedProduct(JsonObject productDBObject) {
    return Option.of(productDBObject)
        .map(product -> {
          JsonObject result = new JsonObject()
              .put("id", productDBObject.getString("id"))
              .put("name", productDBObject.getString("name"))
              .put("description", productDBObject.getString("description"));

          return result;
        })
        .getOrElse(new JsonObject());
  }

  private void fillProductWithPrices(String id, Consumer consumer) {
    WebClient.create(vertx)
        .get(config.getInteger("PRICE_PORT"), "localhost",
            "/price?fields=currency,price&productId=" + id)
        .putHeader("content/type", "application/json")
        .send(ar -> {
          if (ar.succeeded()) {
            HttpResponse<Buffer> response = ar.result();
            consumer.accept(response.bodyAsJsonArray());
          } else {
            System.out.println(ar.cause());
          }
        });
  }
}
