package com.rev.test.transfers.model;

import com.rev.test.transfers.model.enums.BalanceType;
import lombok.Builder;
import lombok.Data;

import java.beans.ConstructorProperties;
import java.math.BigDecimal;
import java.util.Currency;

@Data
@Builder
public class Wallet {
    private BalanceType balanceType;
    private Currency currency;
    private BigDecimal amount;

    @ConstructorProperties({"balanceType", "currency", "amount"})
    public Wallet(BalanceType balanceType, Currency currency, BigDecimal amount) {
        this.balanceType = balanceType;
        this.currency = currency;
        this.amount = amount;
    }
}
