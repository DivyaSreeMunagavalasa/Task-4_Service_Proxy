package com.IoT_DevicesStore.IoT_CRUD;

import com.IoT_DevicesStore.IoT_CRUD.restapi.APIVerticle;
import com.IoT_DevicesStore.IoT_CRUD.database.DatabaseService;
import com.IoT_DevicesStore.IoT_CRUD.database.PostgresVerticle;
import com.IoT_DevicesStore.IoT_CRUD.service.DeviceService;
import com.IoT_DevicesStore.IoT_CRUD.service.DeviceServiceImpl;
import io.vertx.core.Vertx;
import io.vertx.serviceproxy.ServiceBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVerticle {
  private static final Logger logger = LoggerFactory.getLogger(MainVerticle.class);

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    DatabaseService databaseService = new DatabaseService(vertx);
    //DeviceService deviceServiceInstance = new DeviceServiceImpl(databaseService);
    DeviceService deviceServiceInstance = DeviceService.create(databaseService);
    // Deploying PostgresVerticle first.....
    vertx.deployVerticle(new PostgresVerticle(databaseService), res -> {
      if (res.succeeded()) {
        logger.info("PostgresVerticle deployed successfully.");

        // Once PostgresVerticle is deployed, deploying APIVerticle.....
        vertx.deployVerticle(new APIVerticle(deviceServiceInstance), result -> {
          if (result.succeeded()) {
            logger.info("APIVerticle deployed successfully.");

            // After verticles are deployed, register the service proxy
            ServiceBinder binder = new ServiceBinder(vertx);
            DeviceServiceImpl deviceServiceImplInstance = new DeviceServiceImpl(databaseService);
            binder.setAddress(DeviceService.ADDRESS).register(DeviceService.class, deviceServiceImplInstance);

          } else {
            logger.error("Failed to deploy APIVerticle: " + result.cause());
          }
        });

      } else {
        logger.error("Failed to deploy PostgresVerticle: " + res.cause());
      }
    });
  }
}
