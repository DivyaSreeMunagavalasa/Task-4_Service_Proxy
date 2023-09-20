package com.IoT_DevicesStore.IoT_CRUD.service;

import com.IoT_DevicesStore.IoT_CRUD.database.DatabaseService;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceProxyBuilder;

@ProxyGen
@VertxGen
public interface DeviceService {
  String ADDRESS = "service.device";

  static DeviceService create(DatabaseService databaseService) {

    return new DeviceServiceImpl(databaseService);
  }

  static DeviceService createProxy(Vertx vertx, String address) {
    return new ServiceProxyBuilder(vertx)
      .setAddress(address)
      .build(DeviceService.class);
  }

  void addDevice(JsonObject device, Handler<AsyncResult<String>> resultHandler);

  void updateDevice(JsonObject device, Handler<AsyncResult<Void>> resultHandler);

  void getDevice(String deviceId, Handler<AsyncResult<JsonObject>> resultHandler);

  void deleteDevice(String deviceId, Handler<AsyncResult<Void>> resultHandler);
}
