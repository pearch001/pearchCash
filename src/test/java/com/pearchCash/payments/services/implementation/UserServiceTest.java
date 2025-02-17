package com.pearchCash.payments.services.implementation;

import com.pearchCash.payments.dtos.requests.UserRegistrationDto;
import com.pearchCash.payments.exceptions.EmailExistsException;
import com.pearchCash.payments.exceptions.UsernameExistsException;
import com.pearchCash.payments.model.User;
import com.pearchCash.payments.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private final UserRegistrationDto validDto =
            new UserRegistrationDto("user1", "password", "user@example.com");

    @Test
    void registerUser_Success() {
        // Mock dependencies
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // Test
        User result = userService.registerUser(validDto);

        // Verify
        assertNotNull(result);
        assertEquals("user1", result.getUsername());
        assertEquals("hashedPassword", result.getPassword());
        assertEquals("user@example.com", result.getEmail());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_UsernameExists() {
        when(userRepository.existsByUsername(validDto.getUsername())).thenReturn(true);

        assertThrows(UsernameExistsException.class, () -> {
            userService.registerUser(validDto);
        });

        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_EmailExists() {
        when(userRepository.existsByUsername(validDto.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(validDto.getEmail())).thenReturn(true);

        assertThrows(EmailExistsException.class, () -> {
            userService.registerUser(validDto);
        });

        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_PasswordIsEncrypted() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encrypted123");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.registerUser(validDto);

        assertEquals("encrypted123", result.getPassword());
        verify(passwordEncoder).encode("password");
    }

}