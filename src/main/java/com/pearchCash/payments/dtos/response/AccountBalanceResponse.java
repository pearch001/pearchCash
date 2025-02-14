package com.pearchCash.payments.dtos.response;

import com.pearchCash.payments.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountBalanceResponse {
    private BigDecimal balance;
    private Currency currency;

    public BigDecimal balance() {
        return balance;
    }

    public Currency currency() {
        return currency;
    }
}
