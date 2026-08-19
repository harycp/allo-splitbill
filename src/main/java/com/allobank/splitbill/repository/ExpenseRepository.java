package com.allobank.splitbill.repository;

import com.allobank.splitbill.entity.Expense;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, String> {

    @EntityGraph(attributePaths = {"paidByParticipant", "splits", "splits.participant"})
    List<Expense> findByBillGroupId(String billGroupId);
}
