package com.pearchCash.payments.services.implementation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.pearchCash.payments.dtos.response.AccountBalanceResponse;
import com.pearchCash.payments.enums.Currency;
import com.pearchCash.payments.exceptions.AccountAlreadyExistsException;
import com.pearchCash.payments.exceptions.AccountNotFoundException;
import com.pearchCash.payments.model.Account;
import com.pearchCash.payments.model.User;
import com.pearchCash.payments.repositories.AccountRepository;
import com.pearchCash.payments.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    private final String testUsername = "testUser";
    private final User testUser = new User(testUsername, "password", "test@example.com");
    private final Currency testCurrency = Currency.USD;

    @Test
    void createAccount_Success() {
        // Setup
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        when(accountRepository.existsByUserAndCurrency(testUser, testCurrency)).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> {
            Account a = inv.getArgument(0);
            a.setId(1L); // Simulate saved entity
            return a;
        });

        // Execute
        Account result = accountService.createAccount(testUsername, testCurrency);

        // Verify
        assertNotNull(result);
        assertEquals(testUser, result.getUser());
        assertEquals(testCurrency, result.getCurrency());
        assertEquals(BigDecimal.ZERO, result.getBalance());

        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void createAccount_AlreadyExists_ThrowsException() {
        // Setup
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        when(accountRepository.existsByUserAndCurrency(testUser, testCurrency)).thenReturn(true);

        // Execute & Verify
        assertThrows(AccountAlreadyExistsException.class, () -> {
            accountService.createAccount(testUsername, testCurrency);
        });

        verify(accountRepository, never()).save(any());
    }

    @Test
    void getAccountBalanceById_Success() {
        // Setup
        Long accountId = 1L;
        Account testAccount = new Account(testUser, testCurrency, new BigDecimal("100.00"));
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        when(accountRepository.findByIdAndUser(accountId, testUser))
                .thenReturn(Optional.of(testAccount));

        // Execute
        AccountBalanceResponse response = accountService.getAccountBalance(testUsername, accountId);

        // Verify
        assertEquals(testAccount.getBalance(), response.balance());
        assertEquals(testCurrency, response.currency());
    }

    @Test
    void getAccountBalanceById_NotFound_ThrowsException() {
        // Setup
        Long accountId = 1L;
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        when(accountRepository.findByIdAndUser(accountId, testUser)).thenReturn(Optional.empty());

        // Execute & Verify
        assertThrows(AccountNotFoundException.class, () -> {
            accountService.getAccountBalance(testUsername, accountId);
        });
    }

    @Test
    void getAccountPaginated_Success() {
        // Setup
        int limit = 10;
        int offset = 0;
        Page<Account> mockPage = mock(Page.class);

        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        when(accountRepository.findAllByUser(eq(testUser), any(Pageable.class)))
                .thenReturn(mockPage);

        // Execute
        Page<Account> result = accountService.getAccountBalance(testUsername, limit, offset);

        // Verify
        assertSame(mockPage, result);
        verify(accountRepository).findAllByUser(
                eq(testUser),
                argThat(pageable ->
                        pageable.getPageNumber() == offset/limit &&
                                pageable.getPageSize() == limit
                )
        );
    }

    @Test
    void createAccount_UserNotFound_ThrowsException() {
        // Setup
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.empty());

        // Execute & Verify
        assertThrows(NoSuchElementException.class, () -> {
            accountService.createAccount(testUsername, testCurrency);
        });
    }

}