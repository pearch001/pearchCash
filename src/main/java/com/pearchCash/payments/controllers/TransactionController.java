package com.pearchCash.payments.controllers;

import com.pearchCash.payments.dtos.requests.CreateAccountRequest;
import com.pearchCash.payments.dtos.requests.DepositRequest;
import com.pearchCash.payments.dtos.requests.TransferRequest;
import com.pearchCash.payments.dtos.requests.WithdrawRequest;
import com.pearchCash.payments.services.implementation.TransactionsService;
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
    private final TransactionsService transactionsService;

    @PostMapping("/transfer")
    public ResponseEntity<Response> transferBetweenAccounts(@RequestBody TransferRequest request) {
        return new ResponseEntity<>(new Response("00", "Success",new GenericData<>(transactionsService.transfer(request.getFromAccountId(), request.getToAccountId(), request.getAmount()))), HttpStatus.BAD_REQUEST);

    }

    @GetMapping("/{limit}/{offset}/history")
    public ResponseEntity<Response> getAllUserTransactions(@PathVariable("limit") Integer limit,@PathVariable("offset") Integer offset) {
        return new ResponseEntity<>(new Response("00", "Success",new GenericData<>(transactionsService.listTransactions(getUserNameFromContext(),limit,offset))), HttpStatus.BAD_REQUEST);

    }

    @PostMapping("/{accountId}/withdraw")
    public ResponseEntity<Response> getBalance(@PathVariable("accountId") Long accountId, @RequestBody WithdrawRequest withdrawRequest) {
        return new ResponseEntity<>(new Response("00", "Success",new GenericData<>(transactionsService.withdrawal(accountId,withdrawRequest.getCurrency(),withdrawRequest.getAmount()))), HttpStatus.BAD_REQUEST);

    }

    @PostMapping("/deposit/notice")
    public ResponseEntity<Response> getBalance(@RequestBody DepositRequest request) {
        return new ResponseEntity<>(new Response("00", "Success",new GenericData<>(transactionsService.deposit(request.getToAccountId(), request.getCurrency(),request.getAmount()))), HttpStatus.BAD_REQUEST);

    }

    private String getUserNameFromContext(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        return userDetails.getUsername();
    }
}
