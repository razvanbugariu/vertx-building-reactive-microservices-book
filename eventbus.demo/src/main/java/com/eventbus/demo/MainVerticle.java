package com.eventbus.demo;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) {
    vertx.eventBus().<String>consumer("hello", message -> {
      JsonObject json = new JsonObject()
        .put("served-by", this.toString());
      if (message.body().isEmpty()) {
        message.reply(json.put("message", "hello"));
      } else {
        message.reply(json.put("message", "hello " + message.body()));
      }
    });
  }
}
