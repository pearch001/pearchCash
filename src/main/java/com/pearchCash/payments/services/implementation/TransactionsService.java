package com.pearchCash.payments.services.implementation;

import com.pearchCash.payments.enums.Currency;
import com.pearchCash.payments.enums.Status;
import com.pearchCash.payments.enums.TransactionType;
import com.pearchCash.payments.exceptions.*;
import com.pearchCash.payments.model.Account;
import com.pearchCash.payments.model.Transaction;
import com.pearchCash.payments.model.User;
import com.pearchCash.payments.repositories.TransactionsRepository;
import com.pearchCash.payments.services.AccountsService;
import com.pearchCash.payments.utils.OffsetBasedPageRequest;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class TransactionsService {
    private final AccountsService accountService;
    private final UserService userService;
    private final TransactionsRepository transactionRepository;





    @Retryable(
            value = { PessimisticLockingFailureException.class, OptimisticLockingFailureException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 100, multiplier = 2)
    )
    @Transactional
    public Transaction deposit(Long accountId, Currency currency, BigDecimal amount) {
        try {
            Account account = accountService.findByIdAndCurrency(accountId,currency);
            account.setBalance(account.getBalance().add(amount));

            accountService.save(account);

            return createTransaction(amount,TransactionType.DEPOSIT,null,account);

        } catch (ObjectOptimisticLockingFailureException ex) {
            throw ex;
        }
    }


    @Retryable(
            value = { PessimisticLockingFailureException.class, OptimisticLockingFailureException.class },
            maxAttempts = 3, // Total attempts: 1 initial + 2 retries
            backoff = @Backoff(delay = 100, multiplier = 2)
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW) // Start a new transaction for each retry
    public Transaction withdrawal(Long accountId, Currency currency, BigDecimal amount) {
        try {
            Account account = accountService.findByIdAndCurrency(accountId, currency);

            account.setBalance(account.getBalance().subtract(amount));
            accountService.save(account);

            return createTransaction(amount, TransactionType.WITHDRAWAL, account, null);
        } catch (PessimisticLockingFailureException ex) {
            throw ex;
        }
    }


    @Retryable(
            value = { PessimisticLockingFailureException.class, OptimisticLockingFailureException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 100, multiplier = 2)
    )
    @Transactional
    public Transaction transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        try {
            // Existing transfer logic
            return performTransfer(fromAccountId, toAccountId, amount);
        } catch (ObjectOptimisticLockingFailureException ex) {
            throw ex;
        }
    }

    private Transaction performTransfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        // Initial wallet retrieval without lock
        Account fromAccount = accountService.findById(fromAccountId)
                .orElseThrow(() -> new AccountNotFoundException("Sender account not found"));

        // Check balance
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        Account toAccount = accountService.findById(toAccountId)
                .orElseThrow(() -> new AccountNotFoundException("Receiver account not found"));

        if(!(fromAccount.getCurrency().equals(toAccount.getCurrency()))){
            throw new CurrencyMismatchException("Currency MisMatch");
        }



        // Lock wallets in consistent order
        List<Account> lockedWallets = lockWalletsInOrder(fromAccount, toAccount);
        Account lockedSender = lockedWallets.get(0);
        Account lockedRecipient = lockedWallets.get(1);


        // Update balances
        lockedSender.setBalance(lockedSender.getBalance().subtract(amount));
        lockedRecipient.setBalance(lockedRecipient.getBalance().add(amount));

        accountService.saveAll(List.of(lockedSender, lockedRecipient));
        return createTransaction(amount,TransactionType.TRANSFER,lockedSender,lockedRecipient);
    }

    private List<Account> lockWalletsInOrder(Account account1, Account account) {
        // Compare by ID to establish consistent locking order
        if (account1.getId().compareTo(account.getId()) < 0) {
            return List.of(
                    accountService.findByIdWithLock(account1.getId()).orElseThrow(),
                    accountService.findByIdWithLock(account.getId()).orElseThrow()
            );
        } else {
            return List.of(
                    accountService.findByIdWithLock(account.getId()).orElseThrow(),
                    accountService.findByIdWithLock(account1.getId()).orElseThrow()
            );
        }
    }

    @Recover
    public Transaction handleTransferRetryExhausted(Exception ex, User sender,
                                                    String recipientUsername, String currency,
                                                    BigDecimal amount) {
        throw new TransactionFailedException("Transaction failed after multiple retries. Please try again later.");
    }




    private Transaction createTransaction(BigDecimal amount,
                                   TransactionType type, Account from, Account to) {
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setType(type);
        if(from != null){
            transaction.setCurrency(from.getCurrency());
            transaction.setFromAccount(from);
        }

        if(to != null){
            transaction.setCurrency(to.getCurrency());
            transaction.setToAccount(to);
        }


        transaction.setTimestamp(LocalDateTime.now());
        transaction.setStatus(Status.COMPLETED);
        return transactionRepository.save(transaction);
    }

    public Page<Transaction> listTransactions(String username,Integer limit, Integer offset){
        User user = userService.findUserByUsername(username);

        return transactionRepository.findByFromAccount_UserOrToAccount_User(user,user, new OffsetBasedPageRequest(limit,offset));
    }
}
