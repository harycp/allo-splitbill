package com.allobank.splitbill.service;

import com.allobank.splitbill.dto.response.DebtEntryResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

@Component
public class SettlementCalculator {

    private static final BigDecimal THRESHOLD = new BigDecimal("0.005");

    /**
     * Computes the service charge percentage from the ASCII sum of lowercase GitHub username modulo 10.
     */
    public int calculateServiceChargePct(String githubUsername) {
        if (githubUsername == null || githubUsername.isBlank()) {
            return 0;
        }
        int sum = githubUsername.toLowerCase()
                .chars()
                .sum();
        return sum % 10;
    }

    /**
     * Calculates the service charge amount: totalExpenses * (serviceChargePct / 100) using BigDecimal HALF_UP.
     */
    public BigDecimal calculateServiceChargeAmount(BigDecimal totalExpenses, int serviceChargePct) {
        if (totalExpenses == null || totalExpenses.compareTo(BigDecimal.ZERO) == 0 || serviceChargePct <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return totalExpenses.multiply(BigDecimal.valueOf(serviceChargePct))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    /**
     * Simplifies debts using greedy min-cash-flow algorithm to minimize total transactions.
     *
     * @param netBalances Map of participant name/id to net balance (positive = creditor, negative = debtor)
     * @return List of minimal debt settlement transfers
     */
    public List<DebtEntryResponse> simplifyDebts(Map<String, BigDecimal> netBalances) {
        List<DebtEntryResponse> settlements = new ArrayList<>();
        if (netBalances == null || netBalances.isEmpty()) {
            return settlements;
        }

        // Priority queues ordered by absolute balance descending
        PriorityQueue<BalanceEntry> creditors = new PriorityQueue<>(
                Comparator.comparing(BalanceEntry::getBalance).reversed());
        PriorityQueue<BalanceEntry> debtors = new PriorityQueue<>(
                Comparator.comparing(BalanceEntry::getBalance));

        for (Map.Entry<String, BigDecimal> entry : netBalances.entrySet()) {
            BigDecimal balance = entry.getValue() != null ? entry.getValue() : BigDecimal.ZERO;
            if (balance.compareTo(THRESHOLD) > 0) {
                creditors.offer(new BalanceEntry(entry.getKey(), balance));
            } else if (balance.compareTo(THRESHOLD.negate()) < 0) {
                debtors.offer(new BalanceEntry(entry.getKey(), balance));
            }
        }

        while (!creditors.isEmpty() && !debtors.isEmpty()) {
            BalanceEntry creditor = creditors.poll();
            BalanceEntry debtor = debtors.poll();

            BigDecimal debtorOwed = debtor.getBalance().abs();
            BigDecimal creditorOwed = creditor.getBalance();

            BigDecimal settleAmount = debtorOwed.min(creditorOwed);

            if (settleAmount.compareTo(THRESHOLD) > 0) {
                settlements.add(DebtEntryResponse.builder()
                        .from(debtor.getName())
                        .to(creditor.getName())
                        .amount(settleAmount.setScale(2, RoundingMode.HALF_UP))
                        .build());
            }

            BigDecimal remainingCredit = creditorOwed.subtract(settleAmount);
            BigDecimal remainingDebt = debtor.getBalance().add(settleAmount); // debtor.balance is negative

            if (remainingCredit.compareTo(THRESHOLD) > 0) {
                creditors.offer(new BalanceEntry(creditor.getName(), remainingCredit));
            }
            if (remainingDebt.compareTo(THRESHOLD.negate()) < 0) {
                debtors.offer(new BalanceEntry(debtor.getName(), remainingDebt));
            }
        }

        return settlements;
    }

    private static class BalanceEntry {
        private final String name;
        private final BigDecimal balance;

        public BalanceEntry(String name, BigDecimal balance) {
            this.name = name;
            this.balance = balance;
        }

        public String getName() {
            return name;
        }

        public BigDecimal getBalance() {
            return balance;
        }
    }
}
