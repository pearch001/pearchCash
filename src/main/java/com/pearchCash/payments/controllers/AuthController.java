package com.pearchCash.payments.controllers;

import com.pearchCash.payments.config.JwtTokenProvider;
import com.pearchCash.payments.dtos.requests.LoginRequest;
import com.pearchCash.payments.dtos.requests.UserRegistrationDto;
import com.pearchCash.payments.model.User;
import com.pearchCash.payments.services.implementation.UserService;
import com.pearchCash.payments.utils.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            return jwtTokenProvider.generateToken(authentication);
        } catch (AuthenticationException e) {
            return "Invalid username or password!";
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Response> register(@RequestBody UserRegistrationDto loginRequest) {
        User user =  userService.registerUser(loginRequest);
        return new ResponseEntity<>(new Response("00", user.getUsername() + " created"), HttpStatus.BAD_REQUEST);

    }


}
