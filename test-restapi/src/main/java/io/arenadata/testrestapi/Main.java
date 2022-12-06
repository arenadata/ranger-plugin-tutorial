package io.arenadata.testrestapi;
import io.arenadata.testrestapi.services.RestServiceVerticleImpl;
import io.vertx.core.Vertx;

public final class Main {
    public static void main(String[] args) {
        final Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new RestServiceVerticleImpl());
    }
}
