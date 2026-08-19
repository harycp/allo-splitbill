package com.allobank.splitbill.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "participant")
@EntityListeners(AuditingEntityListener.class)
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private BillGroup billGroup;

    @Column(nullable = false, length = 100)
    private String name;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Participant() {
    }

    public Participant(String id, BillGroup billGroup, String name, LocalDateTime createdAt) {
        this.id = id;
        this.billGroup = billGroup;
        this.name = name;
        this.createdAt = createdAt;
    }

    public static ParticipantBuilder builder() {
        return new ParticipantBuilder();
    }

    public static class ParticipantBuilder {
        private String id;
        private BillGroup billGroup;
        private String name;
        private LocalDateTime createdAt;

        public ParticipantBuilder id(String id) {
            this.id = id;
            return this;
        }

        public ParticipantBuilder billGroup(BillGroup billGroup) {
            this.billGroup = billGroup;
            return this;
        }

        public ParticipantBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ParticipantBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Participant build() {
            return new Participant(id, billGroup, name, createdAt);
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
}
