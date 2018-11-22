package com.rev.test.transfers.model.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {
    private String sourceAccountId;
    private String targetAccountId;
    private String currencyCode;
    private BigDecimal amount;
}
