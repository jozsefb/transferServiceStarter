package com.rev.test.transfers.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.rev.test.transfers.model.Account;
import com.rev.test.transfers.model.request.DepositRequest;
import com.rev.test.transfers.service.DepositService;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

@Slf4j
public class DepositHandler implements Handler<RoutingContext> {

    private final DepositService depositService;

    @Inject
    public DepositHandler(DepositService depositService) {
        this.depositService = depositService;
    }

    @Override
    public void handle(RoutingContext context) {
        log.info("Handling a deposit.");
        try {
            DepositRequest depositRequest = readDepositRequest(context);
            Account account = depositService.deposit(depositRequest);
            JsonObject jsonObject = JsonObject.mapFrom(account);
            context.response().setStatusCode(OK.code()).putHeader("content-type",
                    HttpHeaderValues.APPLICATION_JSON.toString()).end(jsonObject.encode());
        } catch (IOException e1) {
            log.error("Failed to parse deposit request.", e1);
            context.response().setStatusCode(BAD_REQUEST.code()).end("Failed to parse deposit request.");
        } catch (Exception e2) {
            log.error("Something went wrong.", e2);
            context.response().setStatusCode(INTERNAL_SERVER_ERROR.code()).end("Something went wrong.");
        }
    }

    private DepositRequest readDepositRequest(RoutingContext context) throws IOException {
        String request = context.getBodyAsString();
        log.debug("Request: {}", request);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(request, DepositRequest.class);
    }
}
