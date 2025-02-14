package com.pearchCash.payments.services.implementation;

import com.pearchCash.payments.enums.Currency;
import com.pearchCash.payments.enums.TransactionType;
import com.pearchCash.payments.exceptions.*;
import com.pearchCash.payments.model.Account;
import com.pearchCash.payments.model.Transaction;
import com.pearchCash.payments.model.User;
import com.pearchCash.payments.repositories.AccountRepository;
import com.pearchCash.payments.repositories.TransactionsRepository;
import com.pearchCash.payments.repositories.UserRepository;
import com.pearchCash.payments.services.PaymentsService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionsService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionsRepository transactionRepository;


    @Transactional
    public Account createAccount(User user, Currency currency) {
        // Check if account already exists for this user/currency
        if (accountRepository.existsByUserAndCurrency(user, currency)) {
            throw new AccountAlreadyExistsException("Account already exists for currency: " + currency);
        }

        // Create new account with zero balance
        Account newAccount = new Account();
        newAccount.setUser(user);
        newAccount.setCurrency(currency);
        newAccount.setBalance(BigDecimal.ZERO);

        // Save the account
        Account savedAccount = accountRepository.save(newAccount);

        return savedAccount;
    }


    @Retryable(
            value = { PessimisticLockingFailureException.class, OptimisticLockingFailureException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 100, multiplier = 2)
    )
    @Transactional
    public void deposit(Long accountId, Currency currency, BigDecimal amount) {
        try {
            Account account = accountRepository.findByIdAndCurrency(accountId,currency)
                    .orElseThrow(() -> new AccountNotFoundException("Account not found"));
            account.setBalance(account.getBalance().add(amount));

            accountRepository.save(account);

            createTransaction(amount,TransactionType.DEPOSIT,null,account);
        } catch (ObjectOptimisticLockingFailureException ex) {
            //logger.warn("Optimistic lock failure detected, attempt will be retried");
            throw ex;
        }
    }


    @Retryable(
            value = { PessimisticLockingFailureException.class, OptimisticLockingFailureException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 100, multiplier = 2)
    )
    @Transactional
    public void withdrawal(Long accountId, Currency currency, BigDecimal amount) {
        try {
            Account account = accountRepository.findByIdAndCurrency(accountId,currency)
                    .orElseThrow(() -> new AccountNotFoundException("Account not found"));
            account.setBalance(account.getBalance().subtract(amount));

            accountRepository.save(account);

            createTransaction(amount,TransactionType.WITHDRAWAL,account,null);
        } catch (ObjectOptimisticLockingFailureException ex) {
            //logger.warn("Optimistic lock failure detected, attempt will be retried");
            throw ex;
        }
    }


    @Retryable(
            value = { PessimisticLockingFailureException.class, OptimisticLockingFailureException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 100, multiplier = 2)
    )
    @Transactional
    public void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        try {
            // Existing transfer logic
            performTransfer(fromAccountId, toAccountId, amount);
        } catch (ObjectOptimisticLockingFailureException ex) {
            //logger.warn("Optimistic lock failure detected, attempt will be retried");
            throw ex;
        }
    }

    private void performTransfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        // Initial wallet retrieval without lock
        Account fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new AccountNotFoundException("Sender account not found"));

        Account toAccount = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new AccountNotFoundException("Receiver account not found"));

        if(!(fromAccount.getCurrency().equals(toAccount.getCurrency()))){
            throw new CurrencyMismatchException("Currency MisMatch");
        }

        // Lock wallets in consistent order
        List<Account> lockedWallets = lockWalletsInOrder(fromAccount, toAccount);
        Account lockedSender = lockedWallets.get(0);
        Account lockedRecipient = lockedWallets.get(1);

        // Check balance after locking
        if (lockedSender.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        // Update balances
        lockedSender.setBalance(lockedSender.getBalance().subtract(amount));
        lockedRecipient.setBalance(lockedRecipient.getBalance().add(amount));

        accountRepository.saveAll(List.of(lockedSender, lockedRecipient));
        createTransaction(amount,TransactionType.TRANSFER,lockedSender,lockedRecipient);
    }

    @Recover
    public Transaction handleTransferRetryExhausted(Exception ex, User sender,
                                                    String recipientUsername, String currency,
                                                    BigDecimal amount) {
        throw new TransactionFailedException("Transaction failed after multiple retries. Please try again later.");
    }


    private List<Account> lockWalletsInOrder(Account account1, Account account) {
        // Compare by ID to establish consistent locking order
        if (account1.getId().compareTo(account.getId()) < 0) {
            return List.of(
                    accountRepository.findByIdWithLock(account1.getId()).orElseThrow(),
                    accountRepository.findByIdWithLock(account.getId()).orElseThrow()
            );
        } else {
            return List.of(
                    accountRepository.findByIdWithLock(account.getId()).orElseThrow(),
                    accountRepository.findByIdWithLock(account1.getId()).orElseThrow()
            );
        }
    }

    private void createTransaction(BigDecimal amount,
                                   TransactionType type, Account from, Account to) {
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setCurrency(from.getCurrency());
        transaction.setFromAccount(from);
        transaction.setToAccount(to);
        transaction.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transaction);
    }

    public Pageable<Transaction> listTransactions(S)
}
