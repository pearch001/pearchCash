package com.pearchCash.payments.dtos.requests;

import com.pearchCash.payments.enums.Currency;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateAccountRequest {
    private Currency currency;

}
