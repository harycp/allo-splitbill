package com.allobank.splitbill.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "expense_share")
public class ExpenseShare {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_id", nullable = false)
    private Expense expense;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", nullable = false)
    private Participant participant;

    @Column(name = "share_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal shareAmount;

    public ExpenseShare() {
    }

    public ExpenseShare(String id, Expense expense, Participant participant, BigDecimal shareAmount) {
        this.id = id;
        this.expense = expense;
        this.participant = participant;
        this.shareAmount = shareAmount;
    }

    public static ExpenseShareBuilder builder() {
        return new ExpenseShareBuilder();
    }

    public static class ExpenseShareBuilder {
        private String id;
        private Expense expense;
        private Participant participant;
        private BigDecimal shareAmount;

        public ExpenseShareBuilder id(String id) {
            this.id = id;
            return this;
        }

        public ExpenseShareBuilder expense(Expense expense) {
            this.expense = expense;
            return this;
        }

        public ExpenseShareBuilder participant(Participant participant) {
            this.participant = participant;
            return this;
        }

        public ExpenseShareBuilder shareAmount(BigDecimal shareAmount) {
            this.shareAmount = shareAmount;
            return this;
        }

        public ExpenseShare build() {
            return new ExpenseShare(id, expense, participant, shareAmount);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Expense getExpense() {
        return expense;
    }

    public void setExpense(Expense expense) {
        this.expense = expense;
    }

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public BigDecimal getShareAmount() {
        return shareAmount;
    }

    public void setShareAmount(BigDecimal shareAmount) {
        this.shareAmount = shareAmount;
    }
}
