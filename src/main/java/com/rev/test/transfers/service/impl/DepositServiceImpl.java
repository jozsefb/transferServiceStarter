package com.rev.test.transfers.service.impl;

import com.google.inject.Inject;
import com.rev.test.transfers.model.Account;
import com.rev.test.transfers.model.Wallet;
import com.rev.test.transfers.model.enums.BalanceType;
import com.rev.test.transfers.model.request.DepositRequest;
import com.rev.test.transfers.service.AccountService;
import com.rev.test.transfers.service.DepositService;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;

@Slf4j
public class DepositServiceImpl implements DepositService {

    private final AccountService accountService;

    @Inject
    public DepositServiceImpl(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public Account deposit(DepositRequest depositRequest) {
        log.debug("Depositing {}{} to {}", depositRequest.getDepositAmount(), depositRequest.getCurrencyCode(), depositRequest.getTargetAccountId());
        Account account = accountService.getAccount(depositRequest.getTargetAccountId());
        Optional<Wallet> optional = account.getWallets().stream()
                .filter(w -> w.getCurrency().getCurrencyCode().equals(depositRequest.getCurrencyCode()))
                .findAny();
        Wallet wallet = optional.orElse(buildEmptyWalletFor(depositRequest.getCurrencyCode()));
        wallet.setAmount(wallet.getAmount().add(depositRequest.getDepositAmount()));
        if (!optional.isPresent()) {
            account.getWallets().add(wallet);
        }
        return account;
    }

    private Wallet buildEmptyWalletFor(String currencyCode) {
        return Wallet.builder()
                .balanceType(BalanceType.DEBIT)
                .currency(Currency.getInstance(currencyCode))
                .amount(BigDecimal.ZERO)
                .build();
    }
}
