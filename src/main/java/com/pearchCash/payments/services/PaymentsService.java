package com.pearchCash.payments.services;

import java.math.BigDecimal;

public interface PaymentsService {
     void deposit(Long accountId, BigDecimal amount);
     void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount);
}
