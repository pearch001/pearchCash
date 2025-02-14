package com.pearchCash.payments.controllers;

import com.pearchCash.payments.config.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private final String validRequest = "{\"username\":\"user\",\"password\":\"password\"}";
    private final String invalidRequest = "{\"username\":\"user\",\"password\":\"wrong\"}";
    private final String emptyUsernameRequest = "{\"username\":\"\",\"password\":\"password\"}";
    private final String emptyPasswordRequest = "{\"username\":\"user\",\"password\":\"\"}";

    @Test
    void login_ValidCredentials_ReturnsJwtToken() throws Exception {
        // Arrange
        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtTokenProvider.generateToken(auth)).thenReturn("valid.token.123");

        // Act & Assert
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest))
                .andExpect(status().isOk())
                .andExpect(content().string("valid.token.123"));
    }

    @Test
    void login_InvalidPassword_ReturnsErrorMessage() throws Exception {
        // Arrange
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isOk())
                .andExpect(content().string("Invalid username or password!"));
    }

    @Test
    void login_EmptyUsername_ReturnsErrorMessage() throws Exception {
        // Arrange
        when(authenticationManager.authenticate(any()))
                .thenThrow(new AuthenticationServiceException("Authentication failed"));

        // Act & Assert
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emptyUsernameRequest))
                .andExpect(status().isOk())
                .andExpect(content().string("Invalid username or password!"));
    }

    @Test
    void login_EmptyPassword_ReturnsErrorMessage() throws Exception {
        // Arrange
        when(authenticationManager.authenticate(any()))
                .thenThrow(new DisabledException("Account disabled"));

        // Act & Assert
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emptyPasswordRequest))
                .andExpect(status().isOk())
                .andExpect(content().string("Invalid username or password!"));
    }

    @Test
    void login_AccountLocked_ReturnsErrorMessage() throws Exception {
        // Arrange
        when(authenticationManager.authenticate(any()))
                .thenThrow(new LockedException("Account locked"));

        // Act & Assert
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest))
                .andExpect(status().isOk())
                .andExpect(content().string("Invalid username or password!"));
    }

}