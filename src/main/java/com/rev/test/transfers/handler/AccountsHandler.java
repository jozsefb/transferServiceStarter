package com.rev.test.transfers.handler;

import com.rev.test.transfers.exception.NotFoundException;
import com.rev.test.transfers.model.Account;
import com.rev.test.transfers.service.AccountService;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import javax.inject.Inject;
import java.util.List;

import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

@Slf4j
public class AccountsHandler implements Handler<RoutingContext> {

    private final AccountService accountService;

    @Inject
    public AccountsHandler(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public void handle(RoutingContext context) {
        log.debug("Handling accounts info request.");
        String id = context.request().getParam("id");
        try {
            if (!Strings.isBlank(id)) {
                Account account = accountService.getAccount(id);
                JsonObject jsonObject = JsonObject.mapFrom(account);
                context.response().setStatusCode(OK.code()).putHeader("content-type",
                        HttpHeaderValues.APPLICATION_JSON.toString()).end(jsonObject.encode());
            } else {
                List<Account> accounts = accountService.getAllAccounts();
                JsonArray jsonArray = new JsonArray();
                accounts.forEach(account -> {
                    JsonObject jsonObject = JsonObject.mapFrom(account);
                    jsonArray.add(jsonObject);
                });
                context.response().setStatusCode(OK.code()).putHeader("content-type",
                        HttpHeaderValues.APPLICATION_JSON.toString()).end(jsonArray.encode());
            }
        } catch (NotFoundException e1) {
            context.response().setStatusCode(NOT_FOUND.code()).end("No such account exists.");
        }
    }
}
