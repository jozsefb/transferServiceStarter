package com.rev.test.transfers.model.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RegistrationRequest {
    private String userName;
    private String currencyCode;
    private BigDecimal depositAmount;
}
