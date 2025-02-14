package com.pearchCash.payments.controllers;

import com.pearchCash.payments.dtos.requests.CreateAccountRequest;
import com.pearchCash.payments.services.implementation.AccountService;
import com.pearchCash.payments.utils.GenericData;
import com.pearchCash.payments.utils.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final AccountService accountService;

    @PostMapping("/transfer")
    public ResponseEntity<Response> createAccount(@RequestBody CreateAccountRequest request) {
        return new ResponseEntity<>(new Response("00", "Success",new GenericData<>(accountService.createAccount(getUserNameFromContext(),request.getCurrency()))), HttpStatus.BAD_REQUEST);

    }

    @GetMapping("/{limit}/{offset}")
    public ResponseEntity<Response> getAllUserAccounts(@PathVariable("limit") Integer limit,@PathVariable("offset") Integer offset) {
        return new ResponseEntity<>(new Response("00", "Success",new GenericData<>(accountService.getUserAccount(getUserNameFromContext(),limit,offset))), HttpStatus.BAD_REQUEST);

    }

    @PostMapping("/{accountId}/balance")
    public ResponseEntity<Response> getBalance( @PathVariable("accountId") Long accountId) {
        return new ResponseEntity<>(new Response("00", "Success",new GenericData<>(accountService.getAccountBalance(getUserNameFromContext(),accountId))), HttpStatus.BAD_REQUEST);

    }

    private String getUserNameFromContext(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        return userDetails.getUsername();
    }
}
