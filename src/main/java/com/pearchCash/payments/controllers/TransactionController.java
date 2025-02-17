package com.pearchCash.payments.controllers;

import com.pearchCash.payments.dtos.requests.DepositRequest;
import com.pearchCash.payments.dtos.requests.TransferRequest;
import com.pearchCash.payments.dtos.requests.WithdrawRequest;
import com.pearchCash.payments.services.PaymentsService;
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
    private final PaymentsService paymentsService;

    @PostMapping("/transfer")
    public ResponseEntity<Response> transferBetweenAccounts(@RequestBody TransferRequest request) {
        return new ResponseEntity<>(new Response("00", "Success",new GenericData<>(paymentsService.transfer(request.getFromAccountId(), request.getToAccountId(), request.getAmount()))), HttpStatus.OK);

    }

    @GetMapping("/{limit}/{offset}/history")
    public ResponseEntity<Response> getAllUserTransactions(@PathVariable("limit") Integer limit,@PathVariable("offset") Integer offset) {
        return new ResponseEntity<>(new Response("00", "Success",new GenericData<>(paymentsService.listTransactions(getUserNameFromContext(),limit,offset))), HttpStatus.OK);

    }

    @PostMapping("/{accountId}/withdraw")
    public ResponseEntity<Response> getBalance(@PathVariable("accountId") Long accountId, @RequestBody WithdrawRequest withdrawRequest) {
        return new ResponseEntity<>(new Response("00", "Success",new GenericData<>(paymentsService.withdrawal(accountId,withdrawRequest.getCurrency(),withdrawRequest.getAmount()))), HttpStatus.OK);

    }

    @PostMapping("/deposit/notice")
    public ResponseEntity<Response> getBalance(@RequestBody DepositRequest request) {
        return new ResponseEntity<>(new Response("00", "Success",new GenericData<>(paymentsService.deposit(request.getToAccountId(), request.getCurrency(),request.getAmount()))), HttpStatus.OK);

    }

    private String getUserNameFromContext(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        return userDetails.getUsername();
    }
}
