package com.rev.test.transfers.verticles;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.rev.test.transfers.di.GuiceModule;
import com.rev.test.transfers.routing.MainRouter;
import io.reactivex.Observable;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.http.HttpServer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainVerticle extends AbstractVerticle {

    @Inject
    private MainRouter mainRouter;

    @Override
    public void start(final Future<Void> startedResult) {
        log.info("Main verticle startup");
        log.debug("Injecting configuration");
        Guice.createInjector(new GuiceModule(vertx)).injectMembers(this);
        vertx.executeBlocking(future -> {
            // Call some blocking API that takes a significant amount of time to return
            log.info("pre loading subscriber alias data");
        }, res -> log.debug("Done"));

        log.info("starting up web server");
        startServer().subscribe(
                t -> {},
                t -> log.error(t.getMessage()),
                () -> {
                    log.info(MainVerticle.class.getName() + " Running on " + getPort() + " !!!!!!! ");
                    startedResult.complete();
                }
        );
    }

    private Observable<HttpServer> startServer() {
        HttpServerOptions options = new HttpServerOptions().setCompressionSupported(true);
        HttpServer httpServer = vertx.createHttpServer(options);
        return httpServer.requestHandler(mainRouter.buildRouter(vertx)::accept).rxListen(config().getInteger("http.port")).toObservable();
    }

    private Integer getPort() {
        return this.getVertx().getOrCreateContext().config().getInteger("http.port");
    }
}
