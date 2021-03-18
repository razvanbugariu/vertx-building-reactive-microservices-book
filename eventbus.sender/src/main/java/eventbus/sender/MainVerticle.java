package eventbus.sender;


import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.core.eventbus.Message;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start() {
        vertx.createHttpServer()
                .requestHandler(req -> {
                    EventBus bus = vertx.eventBus();
                    Single<JsonObject> obs1 = bus
                            .<JsonObject>rxRequest("hello", "Luke")
                            .map(Message::body);
                    Single<JsonObject> obs2 = bus
                            .<JsonObject>rxRequest("hello", "Leia")
                            .map(Message::body);
                    Single.zip(obs1, obs2, (luke, leia) ->
                            new JsonObject()
                                    .put("Luke", luke.getString("message") + " from " + luke.getString("served-by"))
                                    .put("Leia", luke.getString("message") + " from " + luke.getString("served-by"))
                    ).subscribe(
                            x -> req.response().end(x.encodePrettily()),
                            t -> {
                                t.printStackTrace();
                                req.response().setStatusCode(500).end(t.getMessage());
                            }
                    );
                }).listen(8082);
    }

}
