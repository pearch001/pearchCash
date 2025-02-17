package com.pearchCash.payments.services;

import com.pearchCash.payments.dtos.requests.UserRegistrationDto;
import com.pearchCash.payments.dtos.response.AccountBalanceResponse;
import com.pearchCash.payments.enums.Currency;
import com.pearchCash.payments.model.Account;
import com.pearchCash.payments.model.User;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface AccountsService {
    Optional<Account> findByIdWithLock(Long id);
    void saveAll(List<Account> accounts);
    Optional<Account> findById(Long accountId);
    void save(Account account);
    Account findByIdAndCurrency(Long accountId, Currency currency);
    Page<Account> getUserAccount(String username, Integer limit, Integer offset);
    AccountBalanceResponse getAccountBalance(String username, Long id);
    Account createAccount(String username, Currency currency);

}
