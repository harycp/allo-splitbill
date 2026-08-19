package com.allobank.splitbill.service;

import com.allobank.splitbill.dto.request.CreateExpenseRequest;
import com.allobank.splitbill.dto.request.ExpenseSplitRequest;
import com.allobank.splitbill.dto.response.ExpenseResponse;
import com.allobank.splitbill.dto.response.ExpenseShareResponse;
import com.allobank.splitbill.entity.BillGroup;
import com.allobank.splitbill.entity.Expense;
import com.allobank.splitbill.entity.ExpenseShare;
import com.allobank.splitbill.entity.Participant;
import com.allobank.splitbill.exception.BadRequestException;
import com.allobank.splitbill.exception.InvalidSplitException;
import com.allobank.splitbill.exception.ResourceNotFoundException;
import com.allobank.splitbill.repository.BillGroupRepository;
import com.allobank.splitbill.repository.ExpenseRepository;
import com.allobank.splitbill.repository.ParticipantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    private final BillGroupRepository billGroupRepository;
    private final ParticipantRepository participantRepository;
    private final ExpenseRepository expenseRepository;

    public ExpenseService(BillGroupRepository billGroupRepository,
                          ParticipantRepository participantRepository,
                          ExpenseRepository expenseRepository) {
        this.billGroupRepository = billGroupRepository;
        this.participantRepository = participantRepository;
        this.expenseRepository = expenseRepository;
    }

    @Transactional
    public ExpenseResponse createExpense(String groupId, CreateExpenseRequest request) {
        BillGroup billGroup = billGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + groupId));

        // Validate paidBy participant belongs to this group
        Participant payer = participantRepository.findByIdAndBillGroupId(request.getPaidBy(), groupId)
                .orElseThrow(() -> new BadRequestException(
                        "Payer participant with id '" + request.getPaidBy() + "' is not a member of group " + groupId));

        // Validate splits match total amount
        BigDecimal splitTotal = request.getSplits().stream()
                .map(ExpenseSplitRequest::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (splitTotal.compareTo(request.getAmount()) != 0) {
            throw new InvalidSplitException(String.format(
                    "Sum of splits (%s) does not match total expense amount (%s)",
                    splitTotal.toPlainString(),
                    request.getAmount().toPlainString()));
        }

        // Validate all split participants belong to this group
        List<Participant> groupParticipants = participantRepository.findByBillGroupId(groupId);
        Map<String, Participant> participantMap = groupParticipants.stream()
                .collect(Collectors.toMap(Participant::getId, p -> p));

        Expense expense = Expense.builder()
                .billGroup(billGroup)
                .paidByParticipant(payer)
                .description(request.getDescription())
                .amount(request.getAmount())
                .build();

        List<ExpenseShare> shares = request.getSplits().stream()
                .map(splitReq -> {
                    Participant participant = participantMap.get(splitReq.getParticipantId());
                    if (participant == null) {
                        throw new BadRequestException(
                                "Split participant with id '" + splitReq.getParticipantId() + "' is not a member of group " + groupId);
                    }
                    return ExpenseShare.builder()
                            .expense(expense)
                            .participant(participant)
                            .shareAmount(splitReq.getAmount())
                            .build();
                })
                .collect(Collectors.toList());

        expense.setSplits(shares);
        Expense savedExpense = expenseRepository.save(expense);

        return mapToResponse(savedExpense);
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponse> getExpensesByGroupId(String groupId) {
        if (!billGroupRepository.existsById(groupId)) {
            throw new ResourceNotFoundException("Group not found with id: " + groupId);
        }

        List<Expense> expenses = expenseRepository.findByBillGroupId(groupId);
        return expenses.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ExpenseResponse mapToResponse(Expense expense) {
        List<ExpenseShareResponse> splitResponses = expense.getSplits().stream()
                .map(s -> ExpenseShareResponse.builder()
                        .shareId(s.getId())
                        .participantId(s.getParticipant().getId())
                        .participantName(s.getParticipant().getName())
                        .amount(s.getShareAmount())
                        .build())
                .collect(Collectors.toList());

        return ExpenseResponse.builder()
                .expenseId(expense.getId())
                .groupId(expense.getBillGroup().getId())
                .paidBy(expense.getPaidByParticipant().getId())
                .paidByName(expense.getPaidByParticipant().getName())
                .description(expense.getDescription())
                .amount(expense.getAmount())
                .createdAt(expense.getCreatedAt())
                .splits(splitResponses)
                .build();
    }
}
