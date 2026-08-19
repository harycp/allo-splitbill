package com.allobank.splitbill.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ExpenseShareResponse {

    private String shareId;
    private String participantId;
    private String participantName;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal amount;

    public ExpenseShareResponse() {
    }

    public ExpenseShareResponse(String shareId, String participantId, String participantName, BigDecimal amount) {
        this.shareId = shareId;
        this.participantId = participantId;
        this.participantName = participantName;
        this.amount = amount;
    }

    public static ExpenseShareResponseBuilder builder() {
        return new ExpenseShareResponseBuilder();
    }

    public static class ExpenseShareResponseBuilder {
        private String shareId;
        private String participantId;
        private String participantName;
        private BigDecimal amount;

        public ExpenseShareResponseBuilder shareId(String shareId) {
            this.shareId = shareId;
            return this;
        }

        public ExpenseShareResponseBuilder participantId(String participantId) {
            this.participantId = participantId;
            return this;
        }

        public ExpenseShareResponseBuilder participantName(String participantName) {
            this.participantName = participantName;
            return this;
        }

        public ExpenseShareResponseBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public ExpenseShareResponse build() {
            return new ExpenseShareResponse(shareId, participantId, participantName, amount);
        }
    }

    public String getShareId() {
        return shareId;
    }

    public void setShareId(String shareId) {
        this.shareId = shareId;
    }

    public String getParticipantId() {
        return participantId;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
