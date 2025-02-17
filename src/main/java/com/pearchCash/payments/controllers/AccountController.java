package com.pearchCash.payments.controllers;

import com.pearchCash.payments.config.JwtTokenProvider;
import com.pearchCash.payments.dtos.requests.CreateAccountRequest;
import com.pearchCash.payments.dtos.requests.LoginRequest;
import com.pearchCash.payments.dtos.requests.UserRegistrationDto;
import com.pearchCash.payments.model.User;
import com.pearchCash.payments.services.AccountsService;
import com.pearchCash.payments.services.implementation.AccountService;
import com.pearchCash.payments.services.implementation.UserService;
import com.pearchCash.payments.utils.GenericData;
import com.pearchCash.payments.utils.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountsService accountService;

    @PostMapping("")
    public ResponseEntity<Response> createAccount(@RequestBody CreateAccountRequest request) {
        return new ResponseEntity<>(new Response("00", "Success",new GenericData<>(accountService.createAccount(getUserNameFromContext(),request.getCurrency()))), HttpStatus.OK);

    }

    @GetMapping("/{limit}/{offset}")
    public ResponseEntity<Response> getAllUserAccounts(@PathVariable("limit") Integer limit,@PathVariable("offset") Integer offset) {
        return new ResponseEntity<>(new Response("00", "Success",new GenericData<>(accountService.getUserAccount(getUserNameFromContext(),limit,offset))), HttpStatus.OK);

    }

    @PostMapping("/{accountId}/balance")
    public ResponseEntity<Response> getBalance( @PathVariable("accountId") Long accountId) {
        return new ResponseEntity<>(new Response("00", "Success",new GenericData<>(accountService.getAccountBalance(getUserNameFromContext(),accountId))), HttpStatus.OK);

    }

    private String getUserNameFromContext(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        return userDetails.getUsername();
    }
}
