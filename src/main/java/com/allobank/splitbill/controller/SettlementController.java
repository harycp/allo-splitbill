package com.allobank.splitbill.controller;

import com.allobank.splitbill.dto.response.ApiResponse;
import com.allobank.splitbill.dto.response.SettlementResponse;
import com.allobank.splitbill.service.SettlementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/groups/{groupId}/settlement")
public class SettlementController {

    private final SettlementService settlementService;

    public SettlementController(SettlementService settlementService) {
        this.settlementService = settlementService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<SettlementResponse>> getSettlement(@PathVariable String groupId) {
        SettlementResponse response = settlementService.calculateSettlement(groupId);
        return ResponseEntity.ok(ApiResponse.success("Settlement calculated successfully", response));
    }
}
