package com.rev.test.transfers.service.impl;

import com.rev.test.transfers.dao.AccountsDao;
import com.rev.test.transfers.dao.UserDao;
import com.rev.test.transfers.model.Account;
import com.rev.test.transfers.model.User;
import com.rev.test.transfers.model.Wallet;
import com.rev.test.transfers.model.enums.BalanceType;
import com.rev.test.transfers.model.request.RegistrationRequest;
import com.rev.test.transfers.service.AccountService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

public class AccountServiceImpl implements AccountService {

    private final UserDao userDao;
    private final AccountsDao accountsDao;

    @Inject
    public AccountServiceImpl(UserDao userDao, AccountsDao accountsDao) {
        this.userDao = userDao;
        this.accountsDao = accountsDao;
    }

    @Override
    public Account registerUser(RegistrationRequest registrationRequest) {
        Account account = buildAccountFromRegData(registrationRequest);
        return accountsDao.create(account);
    }

    private Account buildAccountFromRegData(RegistrationRequest registrationRequest) {
        User user = saveUserFromRegData(registrationRequest);
        Wallet defaultWallet = Wallet.builder().balanceType(BalanceType.DEBIT)
                .currency(Currency.getInstance(registrationRequest.getCurrencyCode()))
                .amount(registrationRequest.getDepositAmount()).build();
        List<Wallet> wallets = new ArrayList<>();
        wallets.add(defaultWallet);
        return Account.builder()
                .userId(user.getUserId())
                .wallets(wallets)
                .status("ACTIVE").build();
    }

    private User saveUserFromRegData(RegistrationRequest registrationRequest) {
        return userDao.create(new User(registrationRequest.getUserName()));
    }

    @Override
    public List<Account> getAllAccounts() {
        return new ArrayList<>(accountsDao.findAll());
    }

    @Override
    public Account getAccount(String accountId) {
        return accountsDao.read(accountId);
    }
}
