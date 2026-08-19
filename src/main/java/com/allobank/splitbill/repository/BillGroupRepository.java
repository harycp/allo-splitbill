package com.allobank.splitbill.repository;

import com.allobank.splitbill.entity.BillGroup;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BillGroupRepository extends JpaRepository<BillGroup, String> {

    @EntityGraph(attributePaths = {"participants"})
    Optional<BillGroup> findWithParticipantsById(String id);
}
