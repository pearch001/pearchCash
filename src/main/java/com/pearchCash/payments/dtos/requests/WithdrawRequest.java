package com.pearchCash.payments.dtos.requests;

import com.pearchCash.payments.enums.Currency;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class WithdrawRequest {
    private Long fromAccountId;
    private BigDecimal amount;

}
