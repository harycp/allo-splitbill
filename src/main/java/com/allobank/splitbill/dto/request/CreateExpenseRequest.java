package com.allobank.splitbill.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateExpenseRequest {

    @NotBlank(message = "paid_by participant ID is required")
    private String paidBy;

    @NotBlank(message = "description must not be blank")
    private String description;

    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.01", message = "amount must be greater than 0")
    private BigDecimal amount;

    @NotEmpty(message = "splits must not be empty")
    @Valid
    private List<ExpenseSplitRequest> splits;

    public CreateExpenseRequest() {
    }

    public CreateExpenseRequest(String paidBy, String description, BigDecimal amount, List<ExpenseSplitRequest> splits) {
        this.paidBy = paidBy;
        this.description = description;
        this.amount = amount;
        this.splits = splits;
    }

    public static CreateExpenseRequestBuilder builder() {
        return new CreateExpenseRequestBuilder();
    }

    public static class CreateExpenseRequestBuilder {
        private String paidBy;
        private String description;
        private BigDecimal amount;
        private List<ExpenseSplitRequest> splits;

        public CreateExpenseRequestBuilder paidBy(String paidBy) {
            this.paidBy = paidBy;
            return this;
        }

        public CreateExpenseRequestBuilder description(String description) {
            this.description = description;
            return this;
        }

        public CreateExpenseRequestBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public CreateExpenseRequestBuilder splits(List<ExpenseSplitRequest> splits) {
            this.splits = splits;
            return this;
        }

        public CreateExpenseRequest build() {
            return new CreateExpenseRequest(paidBy, description, amount, splits);
        }
    }

    public String getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(String paidBy) {
        this.paidBy = paidBy;
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

    public List<ExpenseSplitRequest> getSplits() {
        return splits;
    }

    public void setSplits(List<ExpenseSplitRequest> splits) {
        this.splits = splits;
    }
}
