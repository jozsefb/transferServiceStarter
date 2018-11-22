package com.rev.test.transfers.service;

import com.rev.test.transfers.model.Account;
import com.rev.test.transfers.model.request.DepositRequest;

public interface DepositService {
    Account deposit(DepositRequest depositRequest);
}
