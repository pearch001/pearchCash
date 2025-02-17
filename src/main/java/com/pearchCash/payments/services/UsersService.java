package com.pearchCash.payments.services;

import com.pearchCash.payments.dtos.requests.UserRegistrationDto;
import com.pearchCash.payments.model.User;

import java.util.Optional;

public interface UsersService {
    User registerUser(UserRegistrationDto dto);
    Optional<User> findByUsername(String username);
}
