package com.allobank.splitbill.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bill_group")
@EntityListeners(AuditingEntityListener.class)
public class BillGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, length = 255)
    private String name;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "billGroup", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Participant> participants = new ArrayList<>();

    @OneToMany(mappedBy = "billGroup", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Expense> expenses = new ArrayList<>();

    public BillGroup() {
    }

    public BillGroup(String id, String name, LocalDateTime createdAt, LocalDateTime updatedAt, List<Participant> participants, List<Expense> expenses) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.participants = participants != null ? participants : new ArrayList<>();
        this.expenses = expenses != null ? expenses : new ArrayList<>();
    }

    public static BillGroupBuilder builder() {
        return new BillGroupBuilder();
    }

    public static class BillGroupBuilder {
        private String id;
        private String name;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<Participant> participants = new ArrayList<>();
        private List<Expense> expenses = new ArrayList<>();

        public BillGroupBuilder id(String id) {
            this.id = id;
            return this;
        }

        public BillGroupBuilder name(String name) {
            this.name = name;
            return this;
        }

        public BillGroupBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public BillGroupBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public BillGroupBuilder participants(List<Participant> participants) {
            this.participants = participants;
            return this;
        }

        public BillGroupBuilder expenses(List<Expense> expenses) {
            this.expenses = expenses;
            return this;
        }

        public BillGroup build() {
            return new BillGroup(id, name, createdAt, updatedAt, participants, expenses);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }
}
