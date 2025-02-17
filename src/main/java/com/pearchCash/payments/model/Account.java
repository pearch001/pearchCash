package com.pearchCash.payments.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pearchCash.payments.enums.Currency;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private Currency currency; // e.g., USD, EUR
    private BigDecimal balance = BigDecimal.ZERO;
    @CreationTimestamp
    private LocalDateTime timestamp;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;



    public Account(User testUser, Currency testCurrency, BigDecimal bigDecimal) {
        this.user = testUser;
        this.currency = testCurrency;
        this.balance = bigDecimal;
    }



    public Account(long l, User user, Currency currency, BigDecimal zero) {
        this.id = l;
        this.user = user;
        this.currency = currency;
        this.balance = zero;
    }
}
