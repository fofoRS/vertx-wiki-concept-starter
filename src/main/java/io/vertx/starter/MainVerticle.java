package io.vertx.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.starter.database.WikiDataBaseVerticle;
import io.vertx.starter.http.HttpServerVerticle;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> promise) {
    Promise<String> deployDataBaseVerticle = Promise.promise();
    vertx.deployVerticle(new WikiDataBaseVerticle(),deployDataBaseVerticle);
    deployDataBaseVerticle
      .future()
      .compose(deploymentId -> {
        Promise<String> deployHttpServerVerticle = Promise.promise();
        vertx.deployVerticle(
          HttpServerVerticle.class.getName(),
          new DeploymentOptions().setInstances(2),deployHttpServerVerticle);
        return deployHttpServerVerticle.future();
      }).setHandler(result -> {
        if(result.succeeded()) {
          System.out.println("Application started up successfully");
          promise.complete();
        } else {
          promise.fail(result.cause());
        }
    }); // chain of future, call the compose if the first future is succeed.
  }
}
