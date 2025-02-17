package com.pearchCash.payments.services.implementation;

import com.pearchCash.payments.dtos.requests.UserRegistrationDto;
import com.pearchCash.payments.exceptions.EmailExistsException;
import com.pearchCash.payments.exceptions.UsernameExistsException;
import com.pearchCash.payments.model.User;
import com.pearchCash.payments.repositories.UserRepository;
import com.pearchCash.payments.services.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsService implements  org.springframework.security.core.userdetails.UserDetailsService {
    private final UserRepository userRepository;



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

}
