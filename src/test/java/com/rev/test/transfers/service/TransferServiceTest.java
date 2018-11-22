package com.rev.test.transfers.service;

import com.rev.test.transfers.dao.impl.AccountsDaoImpl;
import com.rev.test.transfers.dao.impl.UserDaoImpl;
import com.rev.test.transfers.exception.InsufficientBallanceException;
import com.rev.test.transfers.exception.NotFoundException;
import com.rev.test.transfers.model.Account;
import com.rev.test.transfers.model.request.DepositRequest;
import com.rev.test.transfers.model.request.RegistrationRequest;
import com.rev.test.transfers.model.request.TransferRequest;
import com.rev.test.transfers.service.impl.AccountServiceImpl;
import com.rev.test.transfers.service.impl.DepositServiceImpl;
import com.rev.test.transfers.service.impl.TransferServiceImpl;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class TransferServiceTest {
    private AccountService accountService;
    private DepositService depositService;
    private TransferService transferService;
    private String accountId1;
    private String accountId2;

    @Before
    public void setup() {
        accountService = new AccountServiceImpl(new UserDaoImpl(), new AccountsDaoImpl());
        depositService = new DepositServiceImpl(accountService);
        transferService = new TransferServiceImpl(accountService, depositService);
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setUserName("user1");
        registrationRequest.setCurrencyCode("GBP");
        registrationRequest.setDepositAmount(BigDecimal.ZERO);
        accountId1 = accountService.registerUser(registrationRequest).getAccountId();
        registrationRequest.setUserName("user2");
        accountId2 = accountService.registerUser(registrationRequest).getAccountId();
    }

    @Test(expected = NotFoundException.class)
    public void transferFromInexistentAccountTest() {
        //given
        TransferRequest request = new TransferRequest();
        request.setSourceAccountId("missing");
        request.setTargetAccountId(accountId2);
        request.setCurrencyCode("GBP");
        request.setAmount(BigDecimal.ONE);
        //when
        transferService.transfer(request);
        //then
        fail();
    }

    @Test(expected = NotFoundException.class)
    public void transferToInexistentAccountTest() {
        //given
        TransferRequest request = new TransferRequest();
        request.setSourceAccountId(accountId1);
        request.setTargetAccountId("missing");
        request.setCurrencyCode("GBP");
        request.setAmount(BigDecimal.ONE);
        //when
        transferService.transfer(request);
        //then
        fail();
    }

    @Test(expected = InsufficientBallanceException.class)
    public void transferFromInexistentWalletTest() {
        //given
        TransferRequest request = new TransferRequest();
        request.setSourceAccountId(accountId1);
        request.setTargetAccountId(accountId2);
        request.setCurrencyCode("USD");
        request.setAmount(BigDecimal.ONE);
        //when
        transferService.transfer(request);
        //then
        fail();
    }

    @Test(expected = InsufficientBallanceException.class)
    public void transferInsufficientBalanceTest() {
        //given
        TransferRequest request = new TransferRequest();
        request.setSourceAccountId(accountId1);
        request.setTargetAccountId(accountId2);
        request.setCurrencyCode("GBP");
        request.setAmount(BigDecimal.ONE);
        //when
        transferService.transfer(request);
        //then
        fail();
    }

    @Test
    public void transferSuccessTest() {
        //given
        TransferRequest request = new TransferRequest();
        request.setSourceAccountId(accountId1);
        request.setTargetAccountId(accountId2);
        request.setCurrencyCode("GBP");
        request.setAmount(BigDecimal.ONE);
        DepositRequest depositRequest = new DepositRequest();
        depositRequest.setTargetAccountId(accountId1);
        depositRequest.setCurrencyCode("GBP");
        depositRequest.setDepositAmount(BigDecimal.TEN);

        depositService.deposit(depositRequest);
        //when
        transferService.transfer(request);
        //then
        Account account1 = accountService.getAccount(accountId1);
        assertNotNull(account1);
        assertNotNull(account1.getWallets());
        assertEquals(9, account1.getWallets().get(0).getAmount().intValue());
        Account account2 = accountService.getAccount(accountId2);
        assertNotNull(account2);
        assertNotNull(account2.getWallets());
        assertEquals(1, account2.getWallets().get(0).getAmount().intValue());
    }
}
