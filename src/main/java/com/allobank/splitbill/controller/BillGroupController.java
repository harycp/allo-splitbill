package com.allobank.splitbill.controller;

import com.allobank.splitbill.dto.request.CreateGroupRequest;
import com.allobank.splitbill.dto.response.ApiResponse;
import com.allobank.splitbill.dto.response.BillGroupResponse;
import com.allobank.splitbill.service.BillGroupService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/groups")
public class BillGroupController {

    private final BillGroupService billGroupService;

    public BillGroupController(BillGroupService billGroupService) {
        this.billGroupService = billGroupService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BillGroupResponse>> createGroup(@Valid @RequestBody CreateGroupRequest request) {
        BillGroupResponse response = billGroupService.createGroup(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Group created successfully", response));
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<ApiResponse<BillGroupResponse>> getGroupById(@PathVariable String groupId) {
        BillGroupResponse response = billGroupService.getGroupById(groupId);
        return ResponseEntity.ok(ApiResponse.success("Group retrieved successfully", response));
    }
}
