package com.rev.test.transfers.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.rev.test.transfers.exception.InsufficientBallanceException;
import com.rev.test.transfers.exception.NotFoundException;
import com.rev.test.transfers.model.Account;
import com.rev.test.transfers.model.request.TransferRequest;
import com.rev.test.transfers.service.TransferService;
import com.sun.tools.javac.util.Pair;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpResponseStatus.PAYMENT_REQUIRED;

@Slf4j
public class TransferHandler implements Handler<RoutingContext> {

    private final TransferService transferService;

    @Inject
    public TransferHandler(TransferService transferService) {
        this.transferService = transferService;
    }

    @Override
    public void handle(RoutingContext context) {
        log.info("Handling a transfer between accounts.");
        try {
            TransferRequest transferRequest = readTransferRequest(context);
            if (!transferRequest.getSourceAccountId().equals(transferRequest.getTargetAccountId())) {
                Pair<Account, Account> accounts = transferService.transfer(transferRequest);
                JsonObject json = buildJsonFromAccounts(accounts);
                context.response().setStatusCode(OK.code()).putHeader("content-type",
                        HttpHeaderValues.APPLICATION_JSON.toString()).end(json.encode());
            } else {
                context.response().setStatusCode(BAD_REQUEST.code()).end("Bad request");
            }
        } catch (IOException e1) {
            log.error("Failed to parse transfer request.", e1);
            context.response().setStatusCode(BAD_REQUEST.code()).end("Failed to parse transfer request.");
        } catch (NotFoundException e2) {
            log.error("Account not found", e2);
            context.response().setStatusCode(NOT_FOUND.code()).end("Account not found.");
        } catch (InsufficientBallanceException e3) {
            log.error("Insufficient balance in source account.", e3);
            context.response().setStatusCode(PAYMENT_REQUIRED.code()).end("Insufficient balance in source account.");
        } catch (Exception e4) {
            log.error("Something went wrong.", e4);
            context.response().setStatusCode(INTERNAL_SERVER_ERROR.code()).end("Something went wrong.");
        }
    }

    private TransferRequest readTransferRequest(RoutingContext context) throws IOException {
        String request = context.getBodyAsString();
        log.debug("Request: {}", request);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(request, TransferRequest.class);
    }

    private JsonObject buildJsonFromAccounts(Pair<Account, Account> accounts) {
        JsonObject json = new JsonObject();
        json.put("sourceAccount", JsonObject.mapFrom(accounts.fst));
        json.put("destinationAccount", JsonObject.mapFrom(accounts.snd));
        return json;
    }
}
