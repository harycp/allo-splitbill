package com.allobank.splitbill.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ExpenseSplitRequest {

    @NotBlank(message = "participant_id is required")
    private String participantId;

    @NotNull(message = "split amount is required")
    @DecimalMin(value = "0.01", message = "split amount must be greater than 0")
    private BigDecimal amount;

    public ExpenseSplitRequest() {
    }

    public ExpenseSplitRequest(String participantId, BigDecimal amount) {
        this.participantId = participantId;
        this.amount = amount;
    }

    public static ExpenseSplitRequestBuilder builder() {
        return new ExpenseSplitRequestBuilder();
    }

    public static class ExpenseSplitRequestBuilder {
        private String participantId;
        private BigDecimal amount;

        public ExpenseSplitRequestBuilder participantId(String participantId) {
            this.participantId = participantId;
            return this;
        }

        public ExpenseSplitRequestBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public ExpenseSplitRequest build() {
            return new ExpenseSplitRequest(participantId, amount);
        }
    }

    public String getParticipantId() {
        return participantId;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
