package com.IoT_DevicesStore.IoT_CRUD.database;

import com.IoT_DevicesStore.IoT_CRUD.database.DatabaseService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.SQLConnection;

import java.util.List;

public class PostgresVerticle extends AbstractVerticle {

  private final DatabaseService databaseService;

  public PostgresVerticle(DatabaseService databaseService) {

    this.databaseService = databaseService;
  }

  @Override
  public void start() {
    // Consumer for adding a device
    vertx.eventBus().consumer("addDevice", message -> {
      try {
        databaseService.getClient().getConnection(res -> {
          if (res.succeeded()) {
            SQLConnection connection = res.result();
            JsonObject deviceData = new JsonObject(message.body().toString());
            connection.updateWithParams(
              "INSERT INTO devices (\"deviceId\", domain, state, city, location, \"deviceType\") VALUES (?, ?, ?, ?, ?::json, ?)",
              new JsonArray().add(deviceData.getString("deviceId"))
                .add(deviceData.getString("domain"))
                .add(deviceData.getString("state"))
                .add(deviceData.getString("city"))
                .add(deviceData.getJsonObject("location").toString())
                .add(deviceData.getString("deviceType")),
              insertResult -> {
                if (insertResult.succeeded()) {
                  message.reply("Device added successfully");
                } else {
                  message.fail(500, "Error while adding device");
                }
                connection.close();
              }
            );
          } else {
            message.fail(500, "Failed to connect to the database");
          }
        });
      } catch(Exception e) {
        message.fail(500, "Failed to parse device data");
      }
    });

    // Consumer for updating a device
    vertx.eventBus().consumer("updateDevice", message -> {
      try {
        JsonObject deviceData = new JsonObject(message.body().toString());
        String deviceId = deviceData.getString("deviceId");
        databaseService.getClient().getConnection(res -> {
          if (res.succeeded()) {
            SQLConnection connection = res.result();
            connection.updateWithParams(
              "UPDATE devices SET domain = ?, state = ?, city = ?, location = ?::json,\"deviceType\" = ? WHERE \"deviceId\" = ?",
              new JsonArray()
                .add(deviceData.getString("domain"))
                .add(deviceData.getString("state"))
                .add(deviceData.getString("city"))
                .add(deviceData.getJsonObject("location").toString())
                .add(deviceData.getString("deviceType"))
                .add(deviceId),
              updateResult -> {
                if (updateResult.succeeded()) {
                  message.reply("Device updated successfully");
                } else {
                  message.fail(500, "Error while updating device");
                }
                connection.close();
              }
            );
          } else {
            message.fail(500, "Failed to connect to the database");
          }
        });
      } catch(Exception e) {
        message.fail(500, "Failed to parse device data for update");
      }
    });

    // Consumer for getting a device
    vertx.eventBus().consumer("getDevice", message -> {
      try {
        String deviceId = message.body().toString();
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
                    JsonObject deviceData = rows.get(0);
                    message.reply(deviceData.encode());
                  } else {
                    message.fail(404, "Device not found");
                  }
                } else {
                  message.fail(500, "Error while retrieving device");
                }
                connection.close();
              }
            );
          } else {
            message.fail(500, "Failed to connect to the database");
          }
        });
      } catch(Exception e) {
        message.fail(500, "Failed to process get device request");
      }
    });

    // Consumer for deleting a device
    vertx.eventBus().consumer("deleteDevice", message -> {
      try {
        String deviceId = message.body().toString();
        databaseService.getClient().getConnection(res -> {
          if (res.succeeded()) {
            SQLConnection connection = res.result();
            connection.updateWithParams(
              "DELETE FROM devices WHERE \"deviceId\" = ?",
              new JsonArray().add(deviceId),
              deleteResult -> {
                if (deleteResult.succeeded()) {
                  message.reply("Device deleted successfully");
                } else {
                  message.fail(500, "Error while deleting device");
                }
                connection.close();
              }
            );
          } else {
            message.fail(500, "Failed to connect to the database");
          }
        });
      } catch(Exception e) {
        message.fail(500, "Failed to process delete device request");
      }
    });
  }
}
