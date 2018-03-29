package com.ineat.demo.common.client;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.List;
import java.util.function.Consumer;

public class DBClient {

  public static final String RESOURCE_FILE_EXTENSION = ".json";

  public static void consumeData(Vertx vertx, String entity, JsonObject param, List<String> fields, Consumer consumer) {
    vertx.fileSystem().readFile(entity + RESOURCE_FILE_EXTENSION, result -> {
      if (result.succeeded()) {
        JsonArray array = new JsonArray(result.result().toString());
        String paramName = param.getString("name");
        String paramValue = param.getString("value");
        JsonArray datas = new JsonArray();
        for (Object obj : array) {
          JsonObject entry = (JsonObject) obj;
          if (paramValue.equals(entry.getString(paramName))) {
            if (!fields.isEmpty()) {
              datas.add(getFormatedEntry(entry, fields));
            } else {
              datas.add(entry);
            }
          }
        }
        consumer.accept(datas);

      } else {
        System.err.println("Entité inconnue");
      }
    });
  }

  private static JsonObject getFormatedEntry(JsonObject entry, List<String> fields) {
    JsonObject data = new JsonObject();
    fields.forEach(field -> {
      if (entry.containsKey(field)) {
        data.put(field, entry.getString(field));
      }
    });
    return data;
  }
}
