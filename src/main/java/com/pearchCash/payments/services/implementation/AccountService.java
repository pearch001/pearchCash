package com.pearchCash.payments.services.implementation;

import com.pearchCash.payments.dtos.response.AccountBalanceResponse;
import com.pearchCash.payments.enums.Currency;
import com.pearchCash.payments.exceptions.*;
import com.pearchCash.payments.model.Account;
import com.pearchCash.payments.model.User;
import com.pearchCash.payments.repositories.AccountRepository;
import com.pearchCash.payments.services.AccountsService;
import com.pearchCash.payments.services.UsersService;
import com.pearchCash.payments.utils.OffsetBasedPageRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService implements AccountsService {
    private final AccountRepository accountRepository;
    private final UsersService userService;


    @Transactional
    public Account createAccount(String username, Currency currency) {
        //Method to create Account

        //Get User
        User user = userService.findByUsername(username).get();

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

    @Transactional
    public AccountBalanceResponse getAccountBalance(String username, Long id) {
        //Method to get Account Balance

        //Get User
        User user = userService.findByUsername(username).get();

        Optional<Account> accountOptional = accountRepository.findByIdAndUser(id,user);
        // Check if account already exists for this user/currency
        if (accountOptional.isEmpty()) {
            throw new AccountNotFoundException("Account does not exists");
        }

        return new AccountBalanceResponse(accountOptional.get().getBalance(),accountOptional.get().getCurrency());
    }


    @Transactional
    public Page<Account> getUserAccount(String username, Integer limit, Integer offset) {
        //Method to get all user Accounts

        //Get User
        User user = userService.findByUsername(username).get();

        return accountRepository.findAllByUser(user,new OffsetBasedPageRequest(limit, offset));
    }


    public Account findByIdAndCurrency(Long accountId, Currency currency) {
        return accountRepository.findByIdAndCurrency(accountId,currency)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
    }

    public void save(Account account) {
        accountRepository.save(account);
    }

    public Optional<Account> findById(Long accountId) {
        return  accountRepository.findById(accountId);
    }

    public void saveAll(List<Account> accounts) {
        accountRepository.saveAll(accounts);
    }


    public Optional<Account> findByIdWithLock(Long id) {
        return accountRepository.findByIdWithLock(id);
    }
}
