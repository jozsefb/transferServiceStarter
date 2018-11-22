package com.rev.test.transfers.service;

import com.rev.test.transfers.dao.impl.AccountsDaoImpl;
import com.rev.test.transfers.dao.impl.UserDaoImpl;
import com.rev.test.transfers.exception.DuplicateAccountException;
import com.rev.test.transfers.exception.NotFoundException;
import com.rev.test.transfers.model.Account;
import com.rev.test.transfers.model.request.RegistrationRequest;
import com.rev.test.transfers.service.impl.AccountServiceImpl;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class AccountServiceTest {

    private AccountService accountService;
    private RegistrationRequest registrationRequest;

    @Before
    public void setup() {
        accountService = new AccountServiceImpl(new UserDaoImpl(), new AccountsDaoImpl());
        registrationRequest = new RegistrationRequest();
        registrationRequest.setUserName("testUser");
        registrationRequest.setCurrencyCode("GBP");
        registrationRequest.setDepositAmount(BigDecimal.TEN);
    }

    @Test
    public void registerUserTest() {
        //given
        //when
        Account account = accountService.registerUser(registrationRequest);
        //then
        assertNotNull(account);
        assertNotNull(account.getWallets());
        assertEquals(1, account.getWallets().size());
        assertEquals(10, account.getWallets().get(0).getAmount().intValue());
    }

    @Test(expected = DuplicateAccountException.class)
    public void registerExistingUserTest() {
        //given
        accountService.registerUser(registrationRequest);
        //when
        accountService.registerUser(registrationRequest);
        //then
        fail();
    }

    @Test(expected = NotFoundException.class)
    public void readMissingUserTest() {
        //given
        //when
        accountService.getAccount("fakeAccId");
        //then
        fail();
    }

    @Test
    public void readExistingUserTest() {
        //given
        String accId = accountService.registerUser(registrationRequest).getAccountId();
        //when
        Account account = accountService.getAccount(accId);
        //then
        assertNotNull(account);
        assertNotNull(account.getWallets());
        assertEquals(1, account.getWallets().size());
        assertEquals(10, account.getWallets().get(0).getAmount().intValue());
    }

    @Test
    public void readAllAccountsTest() {
        //given
        registrationRequest.setUserName("u1");
        accountService.registerUser(registrationRequest);
        registrationRequest.setUserName("u2");
        accountService.registerUser(registrationRequest);
        registrationRequest.setUserName("u3");
        accountService.registerUser(registrationRequest);
        //when
        Collection<Account> accounts = accountService.getAllAccounts();
        //then
        assertNotNull(accounts);
        assertEquals(3, accounts.size());
    }
}
