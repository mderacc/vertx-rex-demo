package com.ineat.demo.category;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

public class CategoryLauncher {

  public static void main(String[] args) {
    final ClusterManager mgr = new HazelcastClusterManager();
    final VertxOptions options = new VertxOptions().setClusterManager(mgr);
    Vertx.clusteredVertx(options, res -> {
      if (res.succeeded()) {
        final Vertx vertx = res.result();
        //Déploiement du verticle traitant les requêtes HTTP
        vertx.deployVerticle(HttpCategoryVerticle.class.getName());
        // Déploiement du verticle traitant les messages de l'event bus
        vertx.deployVerticle(BusCategoryVerticle.class.getName());

      } else {
        System.out.println("Echec lors du deploiement des verticles");
      }
    });
  }
}
