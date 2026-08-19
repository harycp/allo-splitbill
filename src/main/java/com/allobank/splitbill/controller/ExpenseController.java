package com.allobank.splitbill.controller;

import com.allobank.splitbill.dto.request.CreateExpenseRequest;
import com.allobank.splitbill.dto.response.ApiResponse;
import com.allobank.splitbill.dto.response.ExpenseResponse;
import com.allobank.splitbill.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/groups/{groupId}/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseResponse>> createExpense(
            @PathVariable String groupId,
            @Valid @RequestBody CreateExpenseRequest request) {
        ExpenseResponse response = expenseService.createExpense(groupId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Expense recorded successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> getExpensesByGroupId(@PathVariable String groupId) {
        List<ExpenseResponse> response = expenseService.getExpensesByGroupId(groupId);
        return ResponseEntity.ok(ApiResponse.success("Expenses retrieved successfully", response));
    }
}
