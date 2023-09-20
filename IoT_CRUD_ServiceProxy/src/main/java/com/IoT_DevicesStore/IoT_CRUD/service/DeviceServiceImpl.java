package com.IoT_DevicesStore.IoT_CRUD.service;

import com.IoT_DevicesStore.IoT_CRUD.database.DatabaseService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.SQLConnection;

import java.util.List;

public class DeviceServiceImpl implements DeviceService {

  private final DatabaseService databaseService;

  public DeviceServiceImpl(DatabaseService databaseService) {

    this.databaseService = databaseService;
  }

  @Override
  public void addDevice(JsonObject device, Handler<AsyncResult<String>> resultHandler) {
    databaseService.getClient().getConnection(res -> {
      if (res.succeeded()) {
        SQLConnection connection = res.result();

        connection.updateWithParams(
          "INSERT INTO devices (\"deviceId\", domain, state, city, location, \"deviceType\") VALUES (?, ?, ?, ?, ?::json, ?)",
          new JsonArray()
            .add(device.getString("deviceId"))
            .add(device.getString("domain"))
            .add(device.getString("state"))
            .add(device.getString("city"))
            .add(device.getJsonObject("location").toString())
            .add(device.getString("deviceType")),
          insertResult -> {
            if (insertResult.succeeded()) {
              resultHandler.handle(Future.succeededFuture("Device added successfully"));
            } else {
              resultHandler.handle(Future.failedFuture("Error while adding device"));
            }
            connection.close();
          }
        );
      } else {
        resultHandler.handle(Future.failedFuture("Failed to connect to the database"));
      }
    });
  }

  @Override
  public void updateDevice(JsonObject device, Handler<AsyncResult<Void>> resultHandler) {
    String deviceId = device.getString("deviceId");
    databaseService.getClient().getConnection(res -> {
      if (res.succeeded()) {
        SQLConnection connection = res.result();
        connection.updateWithParams(
          "UPDATE devices SET domain = ?, state = ?, city = ?, location = ?::json,\"deviceType\" = ? WHERE \"deviceId\" = ?",
          new JsonArray()
            .add(device.getString("domain"))
            .add(device.getString("state"))
            .add(device.getString("city"))
            .add(device.getJsonObject("location").toString())
            .add(device.getString("deviceType"))
            .add(deviceId),
          updateResult -> {
            if (updateResult.succeeded()) {
              resultHandler.handle(Future.succeededFuture());
            } else {
              resultHandler.handle(Future.failedFuture("Error while updating device"));
            }
            connection.close();
          }
        );
      } else {
        resultHandler.handle(Future.failedFuture("Failed to connect to the database"));
      }
    });
  }

  @Override
  public void getDevice(String deviceId, Handler<AsyncResult<JsonObject>> resultHandler) {
    databaseService.getClient().getConnection(res -> {
      if (res.succeeded()) {
        SQLConnection connection = res.result();
        connection.queryWithParams(
          "SELECT \"deviceId\", domain, state, city, location::json, \"deviceType\" FROM devices WHERE \"deviceId\" = ?",
          new JsonArray().add(deviceId),
          queryResult -> {
            if (queryResult.succeeded()) {
              List<JsonObject> rows = queryResult.result().getRows();
              if (!rows.isEmpty()) {
                resultHandler.handle(Future.succeededFuture(rows.get(0)));
              } else {
                resultHandler.handle(Future.failedFuture("Device not found"));
              }
            } else {
              resultHandler.handle(Future.failedFuture("Error while retrieving device"));
            }
            connection.close();
          }
        );
      } else {
        resultHandler.handle(Future.failedFuture("Failed to connect to the database"));
      }
    });
  }

  @Override
  public void deleteDevice(String deviceId, Handler<AsyncResult<Void>> resultHandler) {
    databaseService.getClient().getConnection(res -> {
      if (res.succeeded()) {
        SQLConnection connection = res.result();
        connection.updateWithParams(
          "DELETE FROM devices WHERE \"deviceId\" = ?",
          new JsonArray().add(deviceId),
          deleteResult -> {
            if (deleteResult.succeeded()) {
              resultHandler.handle(Future.succeededFuture());
            } else {
              resultHandler.handle(Future.failedFuture("Error while deleting device"));
            }
            connection.close();
          }
        );
      } else {
        resultHandler.handle(Future.failedFuture("Failed to connect to the database"));
      }
    });
  }
}
