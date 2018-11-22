package com.rev.test.transfers.dao;

import com.rev.test.transfers.model.Account;

import java.util.Collection;

public interface AccountsDao {
    Account create(Account account);
    Account read(String accountId);
    Collection<Account> findAll();
}
