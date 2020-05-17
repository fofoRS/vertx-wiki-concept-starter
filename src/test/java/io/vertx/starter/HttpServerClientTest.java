package io.vertx.starter;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class HttpServerClientTest {

  @Test
  public void doHttpCall(TestContext context) {
    Async async = context.async();
    Vertx vertx = Vertx.vertx();
    HttpServer server = vertx.createHttpServer().requestHandler(req -> {
      req.response().putHeader("Content-type","plain/text");
      req.response().end("OK");
    });
    server.listen(8080,handler -> {
      WebClient webClient = WebClient.create(vertx);
      webClient.get(8080,"localhost","/").send(responseHandler -> {
        if(responseHandler.succeeded()) {
          HttpResponse<Buffer> response = responseHandler.result();
          context.assertTrue(response.headers().contains("Content-type"));
          context.assertEquals(response.getHeader("Content-type"),"plain/text");
          context.assertEquals(response.bodyAsString(),"OK");
          webClient.close();
          async.complete();
        } else {
          Promise promise = Promise.promise();
          promise.fail(responseHandler.cause());
          async.resolve(promise);
        }
      });
    });
  }
}
