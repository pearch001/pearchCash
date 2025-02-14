package com.pearchCash.payments.dtos.requests;

import com.pearchCash.payments.enums.Currency;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class TransferRequest {
    private Long toAccountId;
    private Long fromAccountId;
    private BigDecimal amount;

}
