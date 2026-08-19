package com.allobank.splitbill.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DebtEntryResponse {

    private String from;
    private String to;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal amount;

    public DebtEntryResponse() {
    }

    public DebtEntryResponse(String from, String to, BigDecimal amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    public static DebtEntryResponseBuilder builder() {
        return new DebtEntryResponseBuilder();
    }

    public static class DebtEntryResponseBuilder {
        private String from;
        private String to;
        private BigDecimal amount;

        public DebtEntryResponseBuilder from(String from) {
            this.from = from;
            return this;
        }

        public DebtEntryResponseBuilder to(String to) {
            this.to = to;
            return this;
        }

        public DebtEntryResponseBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public DebtEntryResponse build() {
            return new DebtEntryResponse(from, to, amount);
        }
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
