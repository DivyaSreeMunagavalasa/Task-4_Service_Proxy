package com.IoT_CRUD.UpdatedServiceProxy;

import com.IoT_CRUD.UpdatedServiceProxy.APIs.APIVerticle;
import com.IoT_CRUD.UpdatedServiceProxy.service.PostgresVerticle;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
public class TestMainVerticle {

  Vertx vertx;

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    this.vertx = vertx;
    vertx.deployVerticle(new APIVerticle(), ar1 -> {
      if (ar1.succeeded()) {
        vertx.deployVerticle(new PostgresVerticle(), ar2 -> {
          if (ar2.succeeded()) {
            testContext.completeNow();
          } else {
            testContext.failNow(new RuntimeException("Failed to deploy PostgresVerticle"));
          }
        });
      } else {
        testContext.failNow(new RuntimeException("Failed to deploy APIVerticle"));
      }
    });
  }

  @Test
  void verticles_deployed(Vertx vertx, VertxTestContext testContext) {
    testContext.completeNow();
  }
}
