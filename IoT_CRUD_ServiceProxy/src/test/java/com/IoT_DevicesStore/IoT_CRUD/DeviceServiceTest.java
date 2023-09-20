package com.IoT_DevicesStore.IoT_CRUD;

import com.IoT_DevicesStore.IoT_CRUD.database.DatabaseService;
import com.IoT_DevicesStore.IoT_CRUD.service.DeviceService;
import com.IoT_DevicesStore.IoT_CRUD.service.DeviceServiceImpl;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(VertxExtension.class)
public class DeviceServiceTest {
  private DeviceService deviceService;

  @BeforeEach
  void setup(Vertx vertx, VertxTestContext testContext) {
    DatabaseService databaseService = new DatabaseService(vertx);
    deviceService = new DeviceServiceImpl(databaseService);
    testContext.completeNow();
  }

  @Test
  void testAddDevice(VertxTestContext testContext) {
    JsonObject deviceJson = new JsonObject();  // ... fill this as necessary
    deviceService.addDevice(deviceJson, res -> {
      if (res.succeeded()) {
        assertNotNull(res.result());  // Assertions or more detailed checks here
        testContext.completeNow();
      } else {
        testContext.failNow(res.cause());
      }
    });
  }

  @Test
  void testUpdateDevice(VertxTestContext testContext) {
    JsonObject deviceJson = new JsonObject() .put("deviceId", "789-tnssdc-789")
      .put("domain", "smart-irrigation-updated")
      .put("state", "TN-updated")
      .put("city", "Chennai-updated")
      .put("deviceType", "smart-tv");
    deviceService.updateDevice(deviceJson, res -> {
      if (res.succeeded()) {
        testContext.completeNow();
      } else {
        testContext.failNow(res.cause());
      }
    });
  }


  @Test
  void testGetDevice(VertxTestContext testContext) {
    String deviceId = "123-asdasd-123";
    deviceService.getDevice(deviceId, res -> {
      if (res.succeeded()) {
        assertNotNull(res.result());
        assertEquals(deviceId, res.result().getString("deviceId"));
        testContext.completeNow();
      } else {
        testContext.failNow(res.cause());
      }
    });
  }

  @Test
  void testDeleteDevice(VertxTestContext testContext) {
    String deviceId = "010-pjbcdc-010";
    deviceService.deleteDevice(deviceId, res -> {
      if (res.succeeded()) {
        assertNotNull(res.result());
        testContext.completeNow();
      } else {
        testContext.failNow(res.cause());
      }
    });
  }
}
