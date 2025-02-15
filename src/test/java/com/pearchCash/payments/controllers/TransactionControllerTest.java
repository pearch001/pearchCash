package com.pearchCash.payments.controllers;


import com.pearchCash.payments.dtos.requests.TransferRequest;
import com.pearchCash.payments.dtos.requests.WithdrawRequest;
import com.pearchCash.payments.dtos.requests.DepositRequest;
import com.pearchCash.payments.enums.Currency;
import com.pearchCash.payments.model.Transaction;
import com.pearchCash.payments.services.implementation.TransactionsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

class TransactionControllerTest {
    @Mock
    private TransactionsService transactionsService;

    @InjectMocks
    private TransactionController yourController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(yourController).build();
        this.objectMapper = new ObjectMapper(); // For JSON serialization
    }

    @Test
    public void testTransferBetweenAccounts() throws Exception {

        Transaction mockTransaction = new Transaction();
        // Mock the service response
        when(transactionsService.transfer(any(Long.class), any(Long.class), any(BigDecimal.class)))
                .thenReturn(mockTransaction);

        // Create the request payload
        TransferRequest transferRequest = new TransferRequest(1L, 2L, BigDecimal.valueOf(100.0));

        // Send POST request and verify response
        mockMvc.perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("00"))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data.data").value("Transaction completed"));
    }

    @Test
    public void testGetAllUserTransactions() throws Exception {
        Page<Transaction> mockPage = Page.empty();

        // Mock the service response
        when(transactionsService.listTransactions(any(String.class), any(Integer.class), any(Integer.class)))
                .thenReturn(mockPage);

        // Send GET request and verify response
        mockMvc.perform(get("/10/0/history"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("00"))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data.data").value("Transaction history"));
    }

    @Test
    public void testWithdraw() throws Exception {

        Transaction mockTransaction = new Transaction();

        // Mock the service response
        when(transactionsService.withdrawal(any(Long.class), any(Currency.class), any(BigDecimal.class)))
                .thenReturn(mockTransaction);

        // Create the request payload
        WithdrawRequest withdrawRequest = new WithdrawRequest(BigDecimal.valueOf(50.0), Currency.valueOf("USD")) ;

        // Send POST request and verify response
        mockMvc.perform(post("/1/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("00"))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data.data").value("Withdraw completed"));
    }

    @Test
    public void testDeposit() throws Exception {
        Transaction mockTransaction = new Transaction();

        // Mock the service response
        when(transactionsService.deposit(any(Long.class), any(Currency.class), any(BigDecimal.class)))
                .thenReturn(mockTransaction);

        // Create the request payload
        DepositRequest depositRequest = new DepositRequest(1L, BigDecimal.valueOf(200.0), Currency.valueOf( "USD"));

        // Send POST request and verify response
        mockMvc.perform(post("/deposit/notice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("00"))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data.data").value("Deposit completed"));
    }

}