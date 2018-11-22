package com.rev.test.transfers.service;

import com.rev.test.transfers.model.Account;
import com.rev.test.transfers.model.request.RegistrationRequest;

import java.util.List;

public interface AccountService {
    Account registerUser(RegistrationRequest registrationRequest);
    List<Account> getAllAccounts();
    Account getAccount(String accountId);
}
