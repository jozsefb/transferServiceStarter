package com.rev.test.transfers.model.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DepositRequest {
    private String targetAccountId;
    private String currencyCode;
    private BigDecimal depositAmount;
}
