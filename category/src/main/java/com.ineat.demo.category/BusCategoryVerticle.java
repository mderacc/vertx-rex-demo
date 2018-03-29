package com.ineat.demo.category;

import com.ineat.demo.common.client.DBClient;
import com.ineat.demo.common.MainApiVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.ArrayList;

public class BusCategoryVerticle extends MainApiVerticle {

  @Override
  public void start() throws Exception {

    getConfigRetriever(vertx).getConfig( event -> {
      event.result().forEach(entry -> config().put(entry.getKey(), entry.getValue()));
    });

    EventBus eb = vertx.eventBus();
    final MessageConsumer<Long> consumer = eb.consumer("get-product-category");
    consumer.handler(message -> {
      JsonObject query = new JsonObject().put("name", "id").put("value", String.valueOf(message.body()));
      DBClient.consumeData(vertx, config().getString("entity"), query, new ArrayList<>(),
          result -> {
            JsonArray categories = (JsonArray) result;
            JsonObject category = categories.getJsonObject(0);
            message.reply(category);

          }
      );
    });
  }
}
