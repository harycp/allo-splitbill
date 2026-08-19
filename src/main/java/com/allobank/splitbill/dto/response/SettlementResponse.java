package com.allobank.splitbill.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SettlementResponse {

    private String groupId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal totalExpenses;

    private int serviceChargePct;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal serviceChargeAmount;

    private List<DebtEntryResponse> settlements;

    public SettlementResponse() {
    }

    public SettlementResponse(String groupId, BigDecimal totalExpenses, int serviceChargePct, BigDecimal serviceChargeAmount, List<DebtEntryResponse> settlements) {
        this.groupId = groupId;
        this.totalExpenses = totalExpenses;
        this.serviceChargePct = serviceChargePct;
        this.serviceChargeAmount = serviceChargeAmount;
        this.settlements = settlements;
    }

    public static SettlementResponseBuilder builder() {
        return new SettlementResponseBuilder();
    }

    public static class SettlementResponseBuilder {
        private String groupId;
        private BigDecimal totalExpenses;
        private int serviceChargePct;
        private BigDecimal serviceChargeAmount;
        private List<DebtEntryResponse> settlements;

        public SettlementResponseBuilder groupId(String groupId) {
            this.groupId = groupId;
            return this;
        }

        public SettlementResponseBuilder totalExpenses(BigDecimal totalExpenses) {
            this.totalExpenses = totalExpenses;
            return this;
        }

        public SettlementResponseBuilder serviceChargePct(int serviceChargePct) {
            this.serviceChargePct = serviceChargePct;
            return this;
        }

        public SettlementResponseBuilder serviceChargeAmount(BigDecimal serviceChargeAmount) {
            this.serviceChargeAmount = serviceChargeAmount;
            return this;
        }

        public SettlementResponseBuilder settlements(List<DebtEntryResponse> settlements) {
            this.settlements = settlements;
            return this;
        }

        public SettlementResponse build() {
            return new SettlementResponse(groupId, totalExpenses, serviceChargePct, serviceChargeAmount, settlements);
        }
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public BigDecimal getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(BigDecimal totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public int getServiceChargePct() {
        return serviceChargePct;
    }

    public void setServiceChargePct(int serviceChargePct) {
        this.serviceChargePct = serviceChargePct;
    }

    public BigDecimal getServiceChargeAmount() {
        return serviceChargeAmount;
    }

    public void setServiceChargeAmount(BigDecimal serviceChargeAmount) {
        this.serviceChargeAmount = serviceChargeAmount;
    }

    public List<DebtEntryResponse> getSettlements() {
        return settlements;
    }

    public void setSettlements(List<DebtEntryResponse> settlements) {
        this.settlements = settlements;
    }
}
