package com.pearchCash.payments.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pearchCash.payments.config.JwtTokenProvider;
import com.pearchCash.payments.dtos.requests.LoginRequest;
import com.pearchCash.payments.dtos.requests.UserRegistrationDto;
import com.pearchCash.payments.exceptions.UsernameExistsException;
import com.pearchCash.payments.model.User;
import com.pearchCash.payments.services.implementation.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserService userService;

    @Test
    void testLoginSuccess() throws Exception {
        // Arrange: Create a login request
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("user");
        loginRequest.setPassword("password");

        // Create a dummy authentication object
        Authentication dummyAuth = new UsernamePasswordAuthenticationToken("user", "password");

        // When authenticationManager.authenticate is called, return dummyAuth
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(dummyAuth);

        // When jwtTokenProvider.generateToken is called with dummyAuth, return a token string
        when(jwtTokenProvider.generateToken(dummyAuth)).thenReturn("dummy-token");

        // Act & Assert: Perform POST /api/auth/login and expect "dummy-token" as response
        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("dummy-token"));
    }

    @Test
    void testLoginFailure() throws Exception {
        // Arrange: Create a login request with wrong credentials
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("user");
        loginRequest.setPassword("wrongpassword");

        // Simulate authentication failure by throwing BadCredentialsException
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act & Assert: Perform POST /api/auth/login and expect failure message
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk()) // controller returns a String, so status remains 200
                .andExpect(content().string("Invalid username or password!"));
    }

    @Test
    void testRegister() throws Exception {
        // Arrange: Create a user registration DTO
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setUsername("testuser");
        registrationDto.setPassword("password");
        registrationDto.setEmail("testuser@test.com");
        // set any other required fields...

        // Create a dummy user object that would be returned by the userService
        User dummyUser = new User();
        dummyUser.setUsername("testuser");

        // When userService.registerUser is called, return dummyUser
        when(userService.registerUser(any(UserRegistrationDto.class))).thenReturn(dummyUser);

        // Act & Assert: Perform POST /api/auth/register and expect a response with code "00" and message "testuser created"
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00"))
                .andExpect(jsonPath("$.message").value("testuser created"));
    }

    @Test
    void testRegisterDuplicateUsername() throws Exception {
        // Arrange: create a registration DTO with duplicate username
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setUsername("testuser");
        registrationDto.setPassword("password");
        registrationDto.setEmail("test@example.com");
        // set any other required fields...

        // Simulate the service throwing a UsernameExistsException
        when(userService.registerUser(any(UserRegistrationDto.class)))
                .thenThrow(new UsernameExistsException("Username already exists: testuser"));

        // Act & Assert: Perform POST /api/auth/register and expect a BAD_REQUEST response with the error message
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Username already exists: testuser"));
    }

    @Test
    void testRegisterInvalidEmail() throws Exception {
        // Arrange: create a registration DTO with an invalid email format
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setUsername("testuser");
        registrationDto.setPassword("password");
        registrationDto.setEmail("invalid-email");  // invalid format

        // Act & Assert: Perform POST /api/auth/register and expect a 400 (Bad Request)
        // and an error message indicating an invalid email address.
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isBadRequest())
                // Depending on your error response structure, check for a message that mentions "valid email"
                .andExpect(content().string(org.hamcrest.Matchers.containsString("valid email")));

        // Verify that the service was not invoked because validation should fail beforehand.
        verify(userService, never()).registerUser(any(UserRegistrationDto.class));
    }

}