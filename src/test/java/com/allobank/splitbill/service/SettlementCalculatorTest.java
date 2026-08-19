package com.allobank.splitbill.service;

import com.allobank.splitbill.dto.response.DebtEntryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SettlementCalculatorTest {

    private SettlementCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new SettlementCalculator();
    }

    @Test
    @DisplayName("Service charge pct dihitung benar dari ASCII sum username johndoe47")
    void shouldCalculateServiceChargePctCorrectlyForJohnDoe() {
        // "johndoe47" -> j(106)+o(111)+h(104)+n(110)+d(100)+o(111)+e(101)+4(52)+7(55) = 850 % 10 = 0
        assertEquals(0, calculator.calculateServiceChargePct("johndoe47"));
    }

    @ParameterizedTest
    @CsvSource({
            "johndoe47, 0",
            "harycp, 7",
            "allobank, 6",
            "alice, 0",
            "bob, 7",
            "'', 0"
    })
    @DisplayName("Service charge pct dihitung benar untuk berbagai username GitHub")
    void shouldCalculateServiceChargePctForVariousUsernames(String username, int expectedPct) {
        assertEquals(expectedPct, calculator.calculateServiceChargePct(username));
    }

    @Test
    @DisplayName("Service charge amount dihitung akurat dengan BigDecimal HALF_UP")
    void shouldCalculateServiceChargeAmountPrecisely() {
        BigDecimal totalExpenses = new BigDecimal("750000.00");
        int pct = 7; // harycp -> 7%
        BigDecimal amount = calculator.calculateServiceChargeAmount(totalExpenses, pct);

        assertThat(amount).isEqualByComparingTo(new BigDecimal("52500.00"));
    }

    @Test
    @DisplayName("3-orang split merata: Andi bayar 300k, masing-masing hutang 100k")
    void shouldSimplifyDebtsForThreePeopleEqualSplit() {
        // Andi paid 300k for Andi, Budi, Cici
        // Net: Andi +200k, Budi -100k, Cici -100k
        Map<String, BigDecimal> netBalances = new HashMap<>();
        netBalances.put("Andi", new BigDecimal("200000.00"));
        netBalances.put("Budi", new BigDecimal("-100000.00"));
        netBalances.put("Cici", new BigDecimal("-100000.00"));

        List<DebtEntryResponse> settlements = calculator.simplifyDebts(netBalances);

        assertThat(settlements).hasSize(2);

        BigDecimal totalSettled = settlements.stream()
                .map(DebtEntryResponse::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        assertThat(totalSettled).isEqualByComparingTo(new BigDecimal("200000.00"));

        for (DebtEntryResponse entry : settlements) {
            assertThat(entry.getTo()).isEqualTo("Andi");
            assertThat(entry.getAmount()).isEqualByComparingTo(new BigDecimal("100000.00"));
        }
    }

    @Test
    @DisplayName("Contoh skenario Epic: 3-orang dengan pembayaran parsial")
    void shouldSimplifyComplexThreePersonExpense() {
        // Total expense 750k: Andi paid 750k. Split: Andi 375k, Budi 250k, Cici 125k
        // Net: Andi +375k, Budi -250k, Cici -125k
        Map<String, BigDecimal> netBalances = new HashMap<>();
        netBalances.put("Andi", new BigDecimal("375000.00"));
        netBalances.put("Budi", new BigDecimal("-250000.00"));
        netBalances.put("Cici", new BigDecimal("-125000.00"));

        List<DebtEntryResponse> settlements = calculator.simplifyDebts(netBalances);

        assertThat(settlements).hasSize(2);

        DebtEntryResponse budiToAndi = settlements.stream()
                .filter(s -> s.getFrom().equals("Budi") && s.getTo().equals("Andi"))
                .findFirst()
                .orElseThrow();
        assertThat(budiToAndi.getAmount()).isEqualByComparingTo(new BigDecimal("250000.00"));

        DebtEntryResponse ciciToAndi = settlements.stream()
                .filter(s -> s.getFrom().equals("Cici") && s.getTo().equals("Andi"))
                .findFirst()
                .orElseThrow();
        assertThat(ciciToAndi.getAmount()).isEqualByComparingTo(new BigDecimal("125000.00"));
    }

    @Test
    @DisplayName("BigDecimal precision: tidak ada floating point error pada pecahan sen")
    void shouldHandleDecimalAmountPrecisely() {
        // Andi paid 100.00 for 3 people: Andi 33.34, Budi 33.33, Cici 33.33
        // Net: Andi +66.66, Budi -33.33, Cici -33.33
        Map<String, BigDecimal> netBalances = new HashMap<>();
        netBalances.put("Andi", new BigDecimal("66.66"));
        netBalances.put("Budi", new BigDecimal("-33.33"));
        netBalances.put("Cici", new BigDecimal("-33.33"));

        List<DebtEntryResponse> settlements = calculator.simplifyDebts(netBalances);

        assertThat(settlements).hasSize(2);
        for (DebtEntryResponse entry : settlements) {
            assertThat(entry.getTo()).isEqualTo("Andi");
            assertThat(entry.getAmount()).isEqualByComparingTo(new BigDecimal("33.33"));
        }
    }

    @Test
    @DisplayName("Edge Case: 0 expenses atau net balance kosong menghasilkan empty list")
    void shouldReturnEmptySettlementsWhenNoDebts() {
        List<DebtEntryResponse> settlements = calculator.simplifyDebts(Collections.emptyMap());
        assertThat(settlements).isEmpty();
    }

    @Test
    @DisplayName("Edge Case: Semua peserta sudah impas / net balance 0 menghasilkan empty list")
    void shouldReturnEmptySettlementsWhenEveryoneBalanced() {
        Map<String, BigDecimal> netBalances = new HashMap<>();
        netBalances.put("Andi", BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        netBalances.put("Budi", BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));

        List<DebtEntryResponse> settlements = calculator.simplifyDebts(netBalances);
        assertThat(settlements).isEmpty();
    }
}
