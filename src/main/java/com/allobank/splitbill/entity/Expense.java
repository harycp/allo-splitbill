package com.allobank.splitbill.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "expense")
@EntityListeners(AuditingEntityListener.class)
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private BillGroup billGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paid_by_participant_id", nullable = false)
    private Participant paidByParticipant;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ExpenseShare> splits = new ArrayList<>();

    public Expense() {
    }

    public Expense(String id, BillGroup billGroup, Participant paidByParticipant, String description, BigDecimal amount, LocalDateTime createdAt, List<ExpenseShare> splits) {
        this.id = id;
        this.billGroup = billGroup;
        this.paidByParticipant = paidByParticipant;
        this.description = description;
        this.amount = amount;
        this.createdAt = createdAt;
        this.splits = splits != null ? splits : new ArrayList<>();
    }

    public static ExpenseBuilder builder() {
        return new ExpenseBuilder();
    }

    public static class ExpenseBuilder {
        private String id;
        private BillGroup billGroup;
        private Participant paidByParticipant;
        private String description;
        private BigDecimal amount;
        private LocalDateTime createdAt;
        private List<ExpenseShare> splits = new ArrayList<>();

        public ExpenseBuilder id(String id) {
            this.id = id;
            return this;
        }

        public ExpenseBuilder billGroup(BillGroup billGroup) {
            this.billGroup = billGroup;
            return this;
        }

        public ExpenseBuilder paidByParticipant(Participant paidByParticipant) {
            this.paidByParticipant = paidByParticipant;
            return this;
        }

        public ExpenseBuilder description(String description) {
            this.description = description;
            return this;
        }

        public ExpenseBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public ExpenseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ExpenseBuilder splits(List<ExpenseShare> splits) {
            this.splits = splits;
            return this;
        }

        public Expense build() {
            return new Expense(id, billGroup, paidByParticipant, description, amount, createdAt, splits);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BillGroup getBillGroup() {
        return billGroup;
    }

    public void setBillGroup(BillGroup billGroup) {
        this.billGroup = billGroup;
    }

    public Participant getPaidByParticipant() {
        return paidByParticipant;
    }

    public void setPaidByParticipant(Participant paidByParticipant) {
        this.paidByParticipant = paidByParticipant;
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

    public List<ExpenseShare> getSplits() {
        return splits;
    }

    public void setSplits(List<ExpenseShare> splits) {
        this.splits = splits;
    }
}
