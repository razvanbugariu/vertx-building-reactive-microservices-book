package com.hello.service;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    Router router = Router.router(vertx);
    router.get("/").handler(rc -> rc.response().end("Hello from Bugariu!"));
    router.get("/:name").handler(this::hello);

    vertx.createHttpServer()
      .requestHandler(router)
      .listen(8080);
  }

  private void hello(RoutingContext rc) {
    String message = "Hello, Bugariu salutes %s";
    if (null != rc.pathParam("name")) {
      message = String.format(message, rc.pathParam("name"));
    }
    JsonObject json = new JsonObject().put("message", message);
    rc.response()
      .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
      .end(json.encode());
  }
}
