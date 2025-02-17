package com.pearchCash.payments.services;

import com.pearchCash.payments.enums.Currency;
import com.pearchCash.payments.model.Transaction;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

public interface PaymentsService {
     Transaction withdrawal(Long accountId, Currency currency, BigDecimal amount);
     Transaction deposit(Long accountId, Currency currency, BigDecimal amount);
     Page<Transaction> listTransactions(String username, Integer limit, Integer offset);
     Transaction transfer(Long fromAccountId, Long toAccountId, BigDecimal amount);
}
