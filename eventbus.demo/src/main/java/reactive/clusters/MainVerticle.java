package reactive.clusters;

import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start() {
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
