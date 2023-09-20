package com.IoT_DevicesStore.IoT_CRUD;

import com.IoT_DevicesStore.IoT_CRUD.restapi.APIVerticle;
import com.IoT_DevicesStore.IoT_CRUD.service.DeviceService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(VertxExtension.class)
public class APIVerticleTest {
  private WebClient client;

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    client = WebClient.create(vertx, new WebClientOptions().setDefaultHost("localhost").setDefaultPort(8080));

    DeviceService deviceServiceMock = mock(DeviceService.class);

    // Set up  mock behavior here
    doAnswer(invocation -> {
      Handler<AsyncResult<String>> handler = invocation.getArgument(1);
      handler.handle(Future.succeededFuture("Mocked response"));
      return null;
    }).when(deviceServiceMock).addDevice(any(), any());

    vertx.deployVerticle(new APIVerticle(deviceServiceMock), testContext.succeedingThenComplete());
  }

  @Test
  void testAddDeviceEndpoint(Vertx vertx, VertxTestContext testContext) {
    JsonObject deviceJson = new JsonObject()
      .put("deviceId", "789-tnssdc-789")
      .put("domain", "smart-irrigation")
      .put("state", "TN")
      .put("city", "Chennai")
      .put("deviceType", "smart-phone");  // ... Add more device details as needed

    client.post("/devices")
      .sendJsonObject(deviceJson, response -> {
        if (response.succeeded()) {
          assertEquals(201, response.result().statusCode());
          assertEquals("Mocked response", response.result().bodyAsString());
          testContext.completeNow();
        } else {
          testContext.failNow(response.cause());
        }
      });
  }

  @Test
  void testUpdateDeviceEndpoint(Vertx vertx, VertxTestContext testContext) {
    JsonObject deviceUpdateJson = new JsonObject()
      .put("deviceId", "789-tnssdc-789")
      .put("domain", "smart-irrigation-updated")
      .put("state", "TN-updated")
      .put("city", "Chennai-updated")
      .put("deviceType", "smart-tv");

    client.put("/devices/789-tnssdc-789")
      .sendJsonObject(deviceUpdateJson, response -> {
        if (response.succeeded()) {
          assertEquals(204, response.result().statusCode());
          testContext.completeNow();
        } else {
          testContext.failNow(response.cause());
        }
      });
  }

  @Test
  void testGetDeviceEndpoint(Vertx vertx, VertxTestContext testContext) {
    client.get("/devices/789-tnssdc-789")
      .send(response -> {
        if (response.succeeded()) {
          assertEquals(200, response.result().statusCode());
          JsonObject deviceData = response.result().bodyAsJsonObject();
          assertEquals("789-tnssdc-789", deviceData.getString("deviceId"));
          testContext.completeNow();
        } else {
          testContext.failNow(response.cause());
        }
      });
  }

  @Test
  void testDeleteDeviceEndpoint(Vertx vertx, VertxTestContext testContext) {
    client.delete("/devices/789-tnssdc-789")
      .send(response -> {
        if (response.succeeded()) {
          assertEquals(204, response.result().statusCode());
          testContext.completeNow();
        } else {
          testContext.failNow(response.cause());
        }
      });
  }


}
