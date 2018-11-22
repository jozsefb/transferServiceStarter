package com.rev.test.transfers.dao.impl;

import com.rev.test.transfers.dao.AccountsDao;
import com.rev.test.transfers.exception.NotFoundException;
import com.rev.test.transfers.model.Account;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AccountsDaoImpl implements AccountsDao {
    private Map<String, Account> accountMap = new ConcurrentHashMap<>();

    @Override
    public Account create(Account account) {
        account.setAccountId(UUID.randomUUID().toString());
        accountMap.put(account.getAccountId(), account);
        return account;
    }

    @Override
    public Account read(String accountId) {
        Account account = accountMap.get(accountId);
        if (account == null) {
            throw new NotFoundException();
        }
        return account;
    }

    @Override
    public Collection<Account> findAll() {
        return accountMap.values();
    }
    //UPDATE AND DELETE NOT SUPPORTED
}
