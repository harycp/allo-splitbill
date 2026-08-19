package com.allobank.splitbill.repository;

import com.allobank.splitbill.entity.ExpenseShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseShareRepository extends JpaRepository<ExpenseShare, String> {

    List<ExpenseShare> findByExpenseId(String expenseId);
}
