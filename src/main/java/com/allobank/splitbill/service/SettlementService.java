package com.allobank.splitbill.service;

import com.allobank.splitbill.dto.response.DebtEntryResponse;
import com.allobank.splitbill.dto.response.SettlementResponse;
import com.allobank.splitbill.entity.Expense;
import com.allobank.splitbill.entity.ExpenseShare;
import com.allobank.splitbill.entity.Participant;
import com.allobank.splitbill.exception.ResourceNotFoundException;
import com.allobank.splitbill.repository.BillGroupRepository;
import com.allobank.splitbill.repository.ExpenseRepository;
import com.allobank.splitbill.repository.ParticipantRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SettlementService {

    private final BillGroupRepository billGroupRepository;
    private final ParticipantRepository participantRepository;
    private final ExpenseRepository expenseRepository;
    private final SettlementCalculator settlementCalculator;

    @Value("${app.github.username:harycp}")
    private String githubUsername;

    public SettlementService(BillGroupRepository billGroupRepository,
                             ParticipantRepository participantRepository,
                             ExpenseRepository expenseRepository,
                             SettlementCalculator settlementCalculator) {
        this.billGroupRepository = billGroupRepository;
        this.participantRepository = participantRepository;
        this.expenseRepository = expenseRepository;
        this.settlementCalculator = settlementCalculator;
    }

    @Transactional(readOnly = true)
    public SettlementResponse calculateSettlement(String groupId) {
        if (!billGroupRepository.existsById(groupId)) {
            throw new ResourceNotFoundException("Group not found with id: " + groupId);
        }

        List<Participant> participants = participantRepository.findByBillGroupId(groupId);
        List<Expense> expenses = expenseRepository.findByBillGroupId(groupId);

        // Calculate total expenses
        BigDecimal totalExpenses = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        // Initialize net balances for all group participants: net = paid - owed
        Map<String, BigDecimal> netBalances = new HashMap<>();
        for (Participant p : participants) {
            netBalances.put(p.getName(), BigDecimal.ZERO);
        }

        for (Expense expense : expenses) {
            String payerName = expense.getPaidByParticipant().getName();
            netBalances.put(payerName, netBalances.getOrDefault(payerName, BigDecimal.ZERO).add(expense.getAmount()));

            for (ExpenseShare share : expense.getSplits()) {
                String participantName = share.getParticipant().getName();
                netBalances.put(participantName, netBalances.getOrDefault(participantName, BigDecimal.ZERO).subtract(share.getShareAmount()));
            }
        }

        int serviceChargePct = settlementCalculator.calculateServiceChargePct(githubUsername);
        BigDecimal serviceChargeAmount = settlementCalculator.calculateServiceChargeAmount(totalExpenses, serviceChargePct);
        List<DebtEntryResponse> simplifiedSettlements = settlementCalculator.simplifyDebts(netBalances);

        return SettlementResponse.builder()
                .groupId(groupId)
                .totalExpenses(totalExpenses)
                .serviceChargePct(serviceChargePct)
                .serviceChargeAmount(serviceChargeAmount)
                .settlements(simplifiedSettlements)
                .build();
    }
}
