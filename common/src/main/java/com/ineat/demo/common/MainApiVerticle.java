package com.ineat.demo.common;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.Status;
import io.vertx.ext.web.Router;

public class MainApiVerticle extends AbstractVerticle {

  public final Router router = Router.router(vertx);

  @Override
  public void start() throws Exception {
    //Déclaration d'un healthcheck
    HealthCheckHandler healthCheckHandler = HealthCheckHandler.create(vertx);
    healthCheckHandler.register("healthcheck", future -> {
      JsonObject resourceStatus = new JsonObject()
          .put("available-memory", Runtime.getRuntime().maxMemory())
          .put("total-memory", Runtime.getRuntime().totalMemory())
          .put("free-memory", Runtime.getRuntime().freeMemory());
      future.complete(Status.OK(resourceStatus));
    });
    router.get("/health").handler(healthCheckHandler);
  }

  /**
   * Récupération des configurations (fichier, environnement...).
   * @param vertx
   * @return ConfigRetriever Permettant l'accès aux configurations
   */
  public ConfigRetriever getConfigRetriever(Vertx vertx) {
    ConfigStoreOptions fileStore = new ConfigStoreOptions()
        .setType("file")
        .setConfig(new JsonObject().put("path", "config.json"));

    ConfigStoreOptions envStore = new ConfigStoreOptions().setType("env");
    ConfigStoreOptions sysStore = new ConfigStoreOptions().setType("sys");

    final ConfigRetrieverOptions options = new ConfigRetrieverOptions();
    options.addStore(fileStore).addStore(envStore).addStore(sysStore);

    return ConfigRetriever.create(vertx, options);
  }
}
