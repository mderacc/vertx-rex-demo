package com.ineat.demo.product;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

public class ProductLauncher {

  public static void main(String[] args) {
    final ClusterManager mgr = new HazelcastClusterManager();
    final VertxOptions options = new VertxOptions().setClusterManager(mgr);
    Vertx.clusteredVertx(options, res -> {
      if (res.succeeded()) {
        final Vertx vertx = res.result();
        vertx.deployVerticle(MainProductVerticle.class.getName());
      } else {
        System.out.println("Impossible de déployer le verticle !!!");
      }
    });
  }
}
