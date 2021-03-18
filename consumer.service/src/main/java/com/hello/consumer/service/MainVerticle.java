package com.hello.consumer.service;

import io.reactivex.Single;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.client.HttpRequest;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.ext.web.codec.BodyCodec;

public class MainVerticle extends AbstractVerticle {

  private WebClient client;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    client = WebClient.create(vertx);

    Router router = Router.router(vertx);
    router.get("/").handler(this::invokeMyFirstMicroservice);

    vertx.createHttpServer()
      .requestHandler(router)
      .listen(8081);
  }

  private void invokeMyFirstMicroservice(RoutingContext routingContext) {
    HttpRequest<JsonObject> requestRazvan = client
      .get(8080, "localhost", "/Razvan")
      .as(BodyCodec.jsonObject());
    HttpRequest<JsonObject> requestMircea = client
      .get(8080, "localhost", "/Mircea")
      .as(BodyCodec.jsonObject());

    //Single is an observable of one element representing the deferred result of an operation
    Single<JsonObject> s1 = requestRazvan.rxSend().map(HttpResponse::body);
    Single<JsonObject> s2 = requestMircea.rxSend().map(HttpResponse::body);

    //Single.zip takes as input a set of Singles and once they have received values, calls a function with the results.
    //Single.zip returns another Single containing the result of the function
    Single
      .zip(s1, s2, (razvan, mircea) -> {
        return new JsonObject()
          .put("Razvan", razvan.getString("message"))
          .put("Mircea", mircea.getString("message"));
      })
      .subscribe( // we retrieve the result from single, takes two functions as parameters, one for success one for fail.
        result -> routingContext.response().end(result.encodePrettily()),
        error -> {
          error.printStackTrace();
          routingContext.response()
            .setStatusCode(500).end(error.getMessage());
        }
      );
  }

}
