package com.rev.test.transfers.model;

import lombok.Builder;
import lombok.Data;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class Account {
    private String accountId;
    private String userId;
    private String status;
    private List<Wallet> wallets;
    private String description;

    @ConstructorProperties({"accountId", "userId", "status", "wallets", "description"})
    public Account(String accountId, String userId, String status, List<Wallet> wallets, String description) {
        this.accountId = accountId;
        this.userId = userId;
        this.status = status;
        this.wallets = wallets != null ? wallets : new ArrayList<>();
        this.description = description;
    }
}
