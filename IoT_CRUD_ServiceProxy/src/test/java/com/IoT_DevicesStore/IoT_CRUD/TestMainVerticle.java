package com.IoT_DevicesStore.IoT_CRUD;

import com.IoT_DevicesStore.IoT_CRUD.restapi.APIVerticle;
import com.IoT_DevicesStore.IoT_CRUD.database.DatabaseService;
import com.IoT_DevicesStore.IoT_CRUD.database.PostgresVerticle;
import com.IoT_DevicesStore.IoT_CRUD.service.DeviceService;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.mock;

@ExtendWith(VertxExtension.class)
public class TestMainVerticle {

  Vertx vertx;
  DatabaseService databaseService;

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    this.vertx = vertx;
    databaseService = new DatabaseService(vertx);

    DeviceService deviceServiceMock = mock(DeviceService.class);
    CompositeFuture.all(
      vertx.deployVerticle(new APIVerticle(deviceServiceMock)),
      vertx.deployVerticle(new PostgresVerticle(databaseService))
    ).onComplete(testContext.succeedingThenComplete());
  }


  @Test
  void verticles_deployed(Vertx vertx, VertxTestContext testContext) {
    testContext.completeNow();
  }
}
