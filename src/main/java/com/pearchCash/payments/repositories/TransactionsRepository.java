package com.pearchCash.payments.repositories;

import com.pearchCash.payments.model.Transaction;
import com.pearchCash.payments.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionsRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByFromAccount_UserOrToAccount_User(User user1, User user2, Pageable pageable);
}
