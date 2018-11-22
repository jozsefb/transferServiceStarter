package com.rev.test.transfers.verticles;

import com.rev.test.transfers.model.Account;
import com.rev.test.transfers.model.request.DepositRequest;
import com.rev.test.transfers.model.request.RegistrationRequest;
import com.rev.test.transfers.model.request.TransferRequest;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.reactivex.core.Vertx;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(VertxUnitRunner.class)
public class MainVerticleTest {

    private static final int port = 8080;
    private Vertx vertx;

    @Before
    public void setUp(TestContext context) {
        JsonObject config = new JsonObject();
        config.put("http.port", port);
        DeploymentOptions options = new DeploymentOptions().setConfig(config);
        vertx = Vertx.vertx();
        vertx.deployVerticle(MainVerticle.class.getName(), options, context.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void testStatus(TestContext context) {
        final Async async = context.async();

        vertx.createHttpClient().getNow(port, "localhost", "/status",
                response -> response.handler(body -> {
                    context.assertEquals(response.statusCode(), 200);
                    async.complete();
                }));
    }

    @Test
    public void testRegisterNewUser() {
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setUserName("TestUser1");
        registrationRequest.setCurrencyCode("GBP");
        registrationRequest.setDepositAmount(BigDecimal.TEN);
        final String json = Json.encodePrettily(registrationRequest);
        Account account = given().body(json).request().post("/api/account").thenReturn().as(Account.class);
        assertNotNull(account);
        assertNotNull(account.getAccountId());
        assertEquals(1, account.getWallets().size());
        assertEquals(10, account.getWallets().get(0).getAmount().intValue());
        assertEquals("GBP", account.getWallets().get(0).getCurrency().getCurrencyCode());
    }

    @Test
    public void testDeposit() {
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setUserName("TestUser1");
        registrationRequest.setCurrencyCode("GBP");
        registrationRequest.setDepositAmount(BigDecimal.TEN);
        final String json1 = Json.encodePrettily(registrationRequest);

        Account account = given().body(json1).request().post("/api/account").thenReturn().as(Account.class);
        assertNotNull(account);
        assertNotNull(account.getAccountId());

        DepositRequest depositRequest = new DepositRequest();
        depositRequest.setTargetAccountId(account.getAccountId());
        depositRequest.setDepositAmount(BigDecimal.TEN);
        depositRequest.setCurrencyCode("GBP");
        final String json2 = Json.encodePrettily(depositRequest);

        given().body(json2).request().post("/api/deposit")
                .then()
                .assertThat()
                .statusCode(200);

        Account updatedAccount = get("/api/account/" + account.getAccountId()).thenReturn().as(Account.class);
        assertNotNull(updatedAccount);
        assertNotNull(updatedAccount.getWallets());
        assertEquals(1, updatedAccount.getWallets().size());
        assertEquals(20, updatedAccount.getWallets().get(0).getAmount().intValue());
    }

    @Test
    public void testTransfer() {
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setUserName("TestUser1");
        registrationRequest.setCurrencyCode("GBP");
        registrationRequest.setDepositAmount(BigDecimal.TEN);
        final String json1 = Json.encodePrettily(registrationRequest);

        Account account1 = given().body(json1).request().post("/api/account").thenReturn().as(Account.class);
        assertNotNull(account1);
        assertNotNull(account1.getAccountId());
        assertEquals(1, account1.getWallets().size());
        assertEquals(10, account1.getWallets().get(0).getAmount().intValue());

        RegistrationRequest registrationRequest2 = new RegistrationRequest();
        registrationRequest2.setUserName("TestUser2");
        registrationRequest2.setCurrencyCode("GBP");
        registrationRequest2.setDepositAmount(BigDecimal.ZERO);
        final String json2 = Json.encodePrettily(registrationRequest2);

        Account account2 = given().body(json2).request().post("/api/account").thenReturn().as(Account.class);
        assertNotNull(account2);
        assertNotNull(account2.getAccountId());
        assertEquals(1, account2.getWallets().size());
        assertEquals(0, account2.getWallets().get(0).getAmount().intValue());

        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setSourceAccountId(account1.getAccountId());
        transferRequest.setTargetAccountId(account2.getAccountId());
        transferRequest.setCurrencyCode("GBP");
        transferRequest.setAmount(BigDecimal.valueOf(3));
        final String json3 = Json.encodePrettily(transferRequest);

        given().body(json3).request().post("/api/transfer")
                .then()
                .assertThat()
                .statusCode(200);

        Account updatedAccount1 = get("/api/account/" + account1.getAccountId()).thenReturn().as(Account.class);
        assertNotNull(updatedAccount1);
        assertEquals(7, updatedAccount1.getWallets().get(0).getAmount().intValue());
        Account updatedAccount2 = get("/api/account/" + account2.getAccountId()).thenReturn().as(Account.class);
        assertNotNull(updatedAccount2);
        assertEquals(3, updatedAccount2.getWallets().get(0).getAmount().intValue());
    }
}
