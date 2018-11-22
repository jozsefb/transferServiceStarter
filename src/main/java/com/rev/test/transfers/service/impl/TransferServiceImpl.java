package com.rev.test.transfers.service.impl;

import com.google.inject.Inject;
import com.rev.test.transfers.exception.InsufficientBallanceException;
import com.rev.test.transfers.model.Account;
import com.rev.test.transfers.model.Wallet;
import com.rev.test.transfers.model.request.DepositRequest;
import com.rev.test.transfers.model.request.TransferRequest;
import com.rev.test.transfers.service.AccountService;
import com.rev.test.transfers.service.DepositService;
import com.rev.test.transfers.service.TransferService;
import com.sun.tools.javac.util.Pair;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TransferServiceImpl implements TransferService {
    private final AccountService accountService;
    private final DepositService depositService;

    @Inject
    public TransferServiceImpl(AccountService accountService, DepositService depositService) {
        this.accountService = accountService;
        this.depositService = depositService;
    }

    @Override
    public Pair<Account, Account> transfer(TransferRequest transferRequest) {
        Account sourceAccount = accountService.getAccount(transferRequest.getSourceAccountId());
        Account destinationAccount = accountService.getAccount(transferRequest.getTargetAccountId());
        withdrawAmountFromSourceAccount(sourceAccount, transferRequest);
        depositToTargetAccount(destinationAccount, transferRequest);
        return Pair.of(sourceAccount, destinationAccount);
    }

    private void withdrawAmountFromSourceAccount(Account sourceAccount, TransferRequest transferRequest) {
        Wallet wallet = getWalletAndValidateHasEnoughBalanceForTransfer(sourceAccount, transferRequest);
        wallet.setAmount(wallet.getAmount().subtract(transferRequest.getAmount()));
    }

    private Wallet getWalletAndValidateHasEnoughBalanceForTransfer(Account sourceAccount, TransferRequest transferRequest) {
        Wallet wallet = sourceAccount.getWallets().stream()
                .filter(w -> w.getCurrency().getCurrencyCode().equals(transferRequest.getCurrencyCode()))
                .findAny().orElse(null);
        if (wallet == null || wallet.getAmount().compareTo(transferRequest.getAmount()) < 0) {
            throw new InsufficientBallanceException();
        }
        return wallet;
    }

    private void depositToTargetAccount(Account destinationAccount, TransferRequest transferRequest) {
        DepositRequest depositRequest = new DepositRequest();
        depositRequest.setTargetAccountId(destinationAccount.getAccountId());
        depositRequest.setCurrencyCode(transferRequest.getCurrencyCode());
        depositRequest.setDepositAmount(transferRequest.getAmount());
        depositService.deposit(depositRequest);
    }
}
