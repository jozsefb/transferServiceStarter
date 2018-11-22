package com.rev.test.transfers.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rev.test.transfers.exception.DuplicateAccountException;
import com.rev.test.transfers.model.Account;
import com.rev.test.transfers.model.request.RegistrationRequest;
import com.rev.test.transfers.service.AccountService;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.IOException;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;


@Slf4j
public class RegistrationHandler implements Handler<RoutingContext> {

    private final AccountService accountService;

    @Inject
    public RegistrationHandler(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public void handle(RoutingContext context) {
        log.info("Handling account registration.");
        try {
            RegistrationRequest registrationRequest = readRegistrationRequest(context);
            Account account = accountService.registerUser(registrationRequest);
            JsonObject json = JsonObject.mapFrom(account);
            context.response().setStatusCode(CREATED.code()).putHeader("content-type",
                    HttpHeaderValues.APPLICATION_JSON.toString()).end(json.encode());
        } catch (IOException e) {
            log.error("Failed to parse registration request.", e);
            context.response().setStatusCode(BAD_REQUEST.code()).end("Failed to parse registration request.");
        } catch (DuplicateAccountException e1) {
            context.response().setStatusCode(BAD_REQUEST.code()).end("Username taken.");
        } catch (Exception e2) {
            log.error("Something went wrong.", e2);
            context.response().setStatusCode(INTERNAL_SERVER_ERROR.code()).end("Something went wrong.");
        }
    }

    private RegistrationRequest readRegistrationRequest(RoutingContext context) throws IOException {
        String regRequest = context.getBodyAsString();
        log.debug("Request: {}", regRequest);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(regRequest, RegistrationRequest.class);
    }
}
