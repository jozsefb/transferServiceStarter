package com.rev.test.transfers.routing;

import com.google.inject.Inject;
import com.rev.test.transfers.handler.AccountsHandler;
import com.rev.test.transfers.handler.DepositHandler;
import com.rev.test.transfers.handler.RegistrationHandler;
import com.rev.test.transfers.handler.StatusHandler;
import com.rev.test.transfers.handler.TransferHandler;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.ext.web.handler.ResponseTimeHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainRouter {

    private final StatusHandler statusHandler;
    private final RegistrationHandler registrationHandler;
    private final AccountsHandler accountsHandler;
    private final DepositHandler depositHandler;
    private final TransferHandler transferHandler;

    @Inject
    public MainRouter(StatusHandler statusHandler, RegistrationHandler registrationHandler,
                      AccountsHandler accountsHandler, DepositHandler depositHandler, TransferHandler transferHandler) {
        this.statusHandler = statusHandler;
        this.registrationHandler = registrationHandler;
        this.accountsHandler = accountsHandler;
        this.depositHandler = depositHandler;
        this.transferHandler = transferHandler;
    }

    public Router buildRouter(Vertx vertx) {
        log.debug("Building Routing handlers.");
        Router router = Router.router(vertx);

        router.route().handler(ResponseTimeHandler.create());
        router.get("/status").handler(statusHandler);
        router.route("/api/account*").handler(BodyHandler.create());
        router.post("/api/account").handler(registrationHandler);
        router.get("/api/account").handler(accountsHandler);
        router.get("/api/account/:id").handler(accountsHandler);
        router.route("/api/deposit*").handler(BodyHandler.create());
        router.post("/api/deposit").handler(depositHandler);
        router.route("/api/transfer*").handler(BodyHandler.create());
        router.post("/api/transfer").handler(transferHandler);

        return router;
    }
}
