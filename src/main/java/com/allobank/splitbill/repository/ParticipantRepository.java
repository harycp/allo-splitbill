package com.allobank.splitbill.repository;

import com.allobank.splitbill.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, String> {

    List<Participant> findByBillGroupId(String billGroupId);

    Optional<Participant> findByIdAndBillGroupId(String id, String billGroupId);
}
