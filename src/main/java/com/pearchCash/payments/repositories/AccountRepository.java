package com.pearchCash.payments.repositories;

import com.pearchCash.payments.enums.Currency;
import com.pearchCash.payments.model.Account;
import com.pearchCash.payments.model.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    // Add pessimistic locking support
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.id = :id")
    Optional<Account> findByIdWithLock(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE) // Acquire a pessimistic write lock
    @Query("SELECT a FROM Account a WHERE a.id = :accountId AND a.currency = :currency")
    Optional<Account> findByIdAndCurrency(
            @Param("accountId") Long accountId,
            @Param("currency") Currency currency
    );

    boolean existsByUserAndCurrency(User user, Currency currency);

    Optional<Account> findByIdAndUser(Long id, User user);

    Page<Account> findAllByUser(User user, Pageable pageable);
}
