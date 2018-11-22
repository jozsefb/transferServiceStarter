package com.rev.test.transfers.service;

import com.rev.test.transfers.dao.impl.AccountsDaoImpl;
import com.rev.test.transfers.dao.impl.UserDaoImpl;
import com.rev.test.transfers.model.Account;
import com.rev.test.transfers.model.request.DepositRequest;
import com.rev.test.transfers.model.request.RegistrationRequest;
import com.rev.test.transfers.service.impl.AccountServiceImpl;
import com.rev.test.transfers.service.impl.DepositServiceImpl;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DepositServiceTest {
    private AccountService accountService;
    private DepositService depositService;
    private DepositRequest depositRequest;
    private String accountId;

    @Before
    public void setup() {
        accountService = new AccountServiceImpl(new UserDaoImpl(), new AccountsDaoImpl());
        depositService = new DepositServiceImpl(accountService);
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setUserName("testUser");
        registrationRequest.setCurrencyCode("GBP");
        registrationRequest.setDepositAmount(BigDecimal.TEN);
        accountId = accountService.registerUser(registrationRequest).getAccountId();
        depositRequest = new DepositRequest();
        depositRequest.setTargetAccountId(accountId);
        depositRequest.setDepositAmount(BigDecimal.TEN);
    }

    @Test
    public void depositToExistingWalletTest() {
        //given
        depositRequest.setCurrencyCode("GBP");
        //when
        depositService.deposit(depositRequest);
        //then
        Account account = accountService.getAccount(accountId);
        assertNotNull(account);
        assertNotNull(account.getWallets());
        assertEquals(1, account.getWallets().size());
        assertEquals(20, account.getWallets().get(0).getAmount().intValue());
    }

    @Test
    public void depositToMissingWalletTest() {
        //given
        depositRequest.setCurrencyCode("USD");
        //when
        depositService.deposit(depositRequest);
        //then
        Account account = accountService.getAccount(accountId);
        assertNotNull(account);
        assertNotNull(account.getWallets());
        assertEquals(2, account.getWallets().size());
        assertEquals(10, account.getWallets().get(0).getAmount().intValue());
        assertEquals(10, account.getWallets().get(1).getAmount().intValue());
    }
}
