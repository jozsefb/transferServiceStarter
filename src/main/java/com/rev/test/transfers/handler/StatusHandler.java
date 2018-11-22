package com.rev.test.transfers.handler;

import io.vertx.core.Handler;
import io.vertx.reactivex.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;

@Slf4j
public class StatusHandler implements Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext context) {
        log.info("Returning server status.");
        context.response().setStatusCode(OK.code()).end("Ok");
    }
}
