package com.pearchCash.payments.services.implementation;

import com.pearchCash.payments.dtos.requests.UserRegistrationDto;
import com.pearchCash.payments.enums.Roles;
import com.pearchCash.payments.enums.TransactionType;
import com.pearchCash.payments.exceptions.*;
import com.pearchCash.payments.model.Account;
import com.pearchCash.payments.model.Transaction;
import com.pearchCash.payments.model.User;
import com.pearchCash.payments.repositories.AccountRepository;
import com.pearchCash.payments.repositories.TransactionsRepository;
import com.pearchCash.payments.repositories.UserRepository;
import com.pearchCash.payments.services.PaymentsService;
import com.pearchCash.payments.services.UsersService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UsersService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(UserRegistrationDto dto) {
        // Check username existence
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new UsernameExistsException("Username already exists: " + dto.getUsername());
        }

        // Check email existence
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailExistsException("Email already registered: " + dto.getEmail());
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setRole(Roles.ROLE_USER.name());
        return userRepository.save(user);
    }



    public User findUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }


    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
