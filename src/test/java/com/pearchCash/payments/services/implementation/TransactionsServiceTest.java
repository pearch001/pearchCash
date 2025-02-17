package com.pearchCash.payments.services.implementation;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.pearchCash.payments.enums.Currency;
import com.pearchCash.payments.enums.TransactionType;
import com.pearchCash.payments.enums.Status;
import com.pearchCash.payments.exceptions.CurrencyMismatchException;
import com.pearchCash.payments.exceptions.InsufficientBalanceException;
import com.pearchCash.payments.model.Account;
import com.pearchCash.payments.model.Transaction;
import com.pearchCash.payments.model.User;
import com.pearchCash.payments.repositories.AccountRepository;
import com.pearchCash.payments.repositories.TransactionsRepository;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.EnableRetry;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@EnableRetry
class TransactionsServiceTest {
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionsRepository transactionRepository;

    @InjectMocks
    private TransactionsService transactionService;

    @Mock
    private UserService userService;



    private final Currency currency = Currency.USD;
    private final Account testAccount = new Account(1L, new User(), currency, BigDecimal.valueOf(100));
    private final Account testRecipient = new Account(2L, new User(), currency, BigDecimal.valueOf(50));

    // Deposit Tests
    @Test
    void deposit_SuccessOnFirstAttempt() {
        when(accountRepository.findByIdAndCurrency(1L, currency))
                .thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class)))
                .thenReturn(testAccount);

        transactionService.deposit(1L, currency, BigDecimal.TEN);

        verify(accountRepository, times(1)).save(testAccount);
        verify(transactionRepository).save(any(Transaction.class));
    }


    // Transfer Tests
    @Test
    void transfer_SuccessWithLockOrdering() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(testRecipient));
        when(accountRepository.findByIdWithLock(1L)).thenReturn(Optional.of(testAccount));
        when(accountRepository.findByIdWithLock(2L)).thenReturn(Optional.of(testRecipient));

        transactionService.transfer(1L, 2L, BigDecimal.valueOf(50));

        verify(accountRepository).findByIdWithLock(1L);
        verify(accountRepository).findByIdWithLock(2L);
        verify(transactionRepository).save(any(Transaction.class));
    }



    // Exception Handling Tests
    @Test
    void transfer_ThrowsCurrencyMismatch() {
        Account eurAccount = new Account(3L, new User(), Currency.EUR, BigDecimal.TEN);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(accountRepository.findById(3L)).thenReturn(Optional.of(eurAccount));

        assertThrows(CurrencyMismatchException.class, () ->
                transactionService.transfer(1L, 3L, BigDecimal.ONE)
        );
    }

    @Test
    void transfer_ThrowsInsufficientBalance() {
        Account poorAccount = new Account(4L, new User(), currency, BigDecimal.ZERO);
        // Stub both initial and locking calls if necessary.
        when(accountRepository.findById(4L)).thenReturn(Optional.of(poorAccount));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(testRecipient));

        // Stub the locking methods as well:
        when(accountRepository.findByIdWithLock(4L)).thenReturn(Optional.of(poorAccount));
        when(accountRepository.findByIdWithLock(2L)).thenReturn(Optional.of(testRecipient));

        assertThrows(InsufficientBalanceException.class, () ->
                transactionService.transfer(4L, 2L, BigDecimal.ONE));
    }



}