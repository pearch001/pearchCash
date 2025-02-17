package com.pearchCash.payments.model;

import com.pearchCash.payments.enums.Currency;
import com.pearchCash.payments.enums.Status;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.pearchCash.payments.enums.TransactionType;


@Data
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private TransactionType type; // DEPOSIT, WITHDRAWAL, TRANSFER
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private Currency currency;
    @Enumerated(EnumType.STRING)
    private Status status;
    @ManyToOne
    private Account fromAccount;
    @ManyToOne
    private Account toAccount;
    private LocalDateTime timestamp;
}
