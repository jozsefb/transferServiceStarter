package com.rev.test.transfers.service;

import com.rev.test.transfers.model.Account;
import com.rev.test.transfers.model.request.TransferRequest;
import com.sun.tools.javac.util.Pair;

public interface TransferService {
    Pair<Account, Account> transfer(TransferRequest transferRequest);
}
