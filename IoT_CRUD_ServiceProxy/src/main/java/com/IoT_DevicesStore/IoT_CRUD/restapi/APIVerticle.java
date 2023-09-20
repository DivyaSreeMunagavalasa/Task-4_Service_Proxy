package com.IoT_DevicesStore.IoT_CRUD.restapi;

import com.IoT_DevicesStore.IoT_CRUD.service.DeviceService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class APIVerticle extends AbstractVerticle {
  private DeviceService deviceService;
  public APIVerticle(DeviceService deviceService) {
    this.deviceService = deviceService;
  }

  @Override
  public void start(Promise<Void> startPromise) {
    // Initialize the proxy

   deviceService = DeviceService.createProxy(vertx, DeviceService.ADDRESS);
    Router router = Router.router(vertx);

    router.route().handler(BodyHandler.create());
    router.post("/devices").handler(this::addDevice);
    router.put("/devices/:deviceId").handler(this::updateDevice);
    router.get("/devices/:deviceId").handler(this::getDevice);
    router.delete("/devices/:deviceId").handler(this::deleteDevice);

    vertx.createHttpServer().requestHandler(router).listen(8080, result -> {
      if (result.succeeded()) {
        startPromise.complete();
      } else {
        startPromise.fail(result.cause());
      }
    });
  }

  private void addDevice(RoutingContext context) {
    JsonObject deviceData = context.getBodyAsJson();
    vertx.eventBus().request("addDevice", deviceData, reply -> {
      if(reply.succeeded()) {
        context.response().setStatusCode(201).end(reply.result().body().toString());
      } else {
        context.response().setStatusCode(500).end();
      }
    });
  }

  private void updateDevice(RoutingContext context) {
    String deviceId = context.request().getParam("deviceId");
    JsonObject updatedData = context.getBodyAsJson().put("deviceId", deviceId);
    vertx.eventBus().request("updateDevice", updatedData, reply -> {
      if(reply.succeeded()) {
        context.response().setStatusCode(204).end();
      } else {
        context.response().setStatusCode(500).end();
      }
    });
  }

  private void getDevice(RoutingContext context) {
    String deviceId = context.request().getParam("deviceId");
    vertx.eventBus().request("getDevice", deviceId, reply -> {
      if(reply.succeeded()) {
        context.response().setStatusCode(200).end(reply.result().body().toString());
      } else {
        context.response().setStatusCode(404).end();
      }
    });
  }

  private void deleteDevice(RoutingContext context) {
    String deviceId = context.request().getParam("deviceId");
    vertx.eventBus().request("deleteDevice", deviceId, reply -> {
      if(reply.succeeded()) {
        context.response().setStatusCode(204).end();
      } else {
        context.response().setStatusCode(500).end();
      }
    });
  }
}
