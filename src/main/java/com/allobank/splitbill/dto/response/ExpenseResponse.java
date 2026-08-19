package com.allobank.splitbill.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ExpenseResponse {

    private String expenseId;
    private String groupId;
    private String paidBy;
    private String paidByName;
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal amount;

    private LocalDateTime createdAt;
    private List<ExpenseShareResponse> splits;

    public ExpenseResponse() {
    }

    public ExpenseResponse(String expenseId, String groupId, String paidBy, String paidByName, String description, BigDecimal amount, LocalDateTime createdAt, List<ExpenseShareResponse> splits) {
        this.expenseId = expenseId;
        this.groupId = groupId;
        this.paidBy = paidBy;
        this.paidByName = paidByName;
        this.description = description;
        this.amount = amount;
        this.createdAt = createdAt;
        this.splits = splits;
    }

    public static ExpenseResponseBuilder builder() {
        return new ExpenseResponseBuilder();
    }

    public static class ExpenseResponseBuilder {
        private String expenseId;
        private String groupId;
        private String paidBy;
        private String paidByName;
        private String description;
        private BigDecimal amount;
        private LocalDateTime createdAt;
        private List<ExpenseShareResponse> splits;

        public ExpenseResponseBuilder expenseId(String expenseId) {
            this.expenseId = expenseId;
            return this;
        }

        public ExpenseResponseBuilder groupId(String groupId) {
            this.groupId = groupId;
            return this;
        }

        public ExpenseResponseBuilder paidBy(String paidBy) {
            this.paidBy = paidBy;
            return this;
        }

        public ExpenseResponseBuilder paidByName(String paidByName) {
            this.paidByName = paidByName;
            return this;
        }

        public ExpenseResponseBuilder description(String description) {
            this.description = description;
            return this;
        }

        public ExpenseResponseBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public ExpenseResponseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ExpenseResponseBuilder splits(List<ExpenseShareResponse> splits) {
            this.splits = splits;
            return this;
        }

        public ExpenseResponse build() {
            return new ExpenseResponse(expenseId, groupId, paidBy, paidByName, description, amount, createdAt, splits);
        }
    }

    public String getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(String expenseId) {
        this.expenseId = expenseId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(String paidBy) {
        this.paidBy = paidBy;
    }

    public String getPaidByName() {
        return paidByName;
    }

    public void setPaidByName(String paidByName) {
        this.paidByName = paidByName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<ExpenseShareResponse> getSplits() {
        return splits;
    }

    public void setSplits(List<ExpenseShareResponse> splits) {
        this.splits = splits;
    }
}
