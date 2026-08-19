package com.allobank.splitbill.service;

import com.allobank.splitbill.dto.request.CreateGroupRequest;
import com.allobank.splitbill.dto.response.BillGroupResponse;
import com.allobank.splitbill.dto.response.ParticipantResponse;
import com.allobank.splitbill.entity.BillGroup;
import com.allobank.splitbill.entity.Participant;
import com.allobank.splitbill.exception.ResourceNotFoundException;
import com.allobank.splitbill.repository.BillGroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BillGroupService {

    private final BillGroupRepository billGroupRepository;

    public BillGroupService(BillGroupRepository billGroupRepository) {
        this.billGroupRepository = billGroupRepository;
    }

    @Transactional
    public BillGroupResponse createGroup(CreateGroupRequest request) {
        BillGroup billGroup = BillGroup.builder()
                .name(request.getName())
                .build();

        if (request.getParticipants() != null) {
            List<Participant> participants = request.getParticipants().stream()
                    .map(pReq -> Participant.builder()
                            .name(pReq.getName())
                            .billGroup(billGroup)
                            .build())
                    .collect(Collectors.toList());
            billGroup.setParticipants(participants);
        }

        BillGroup savedGroup = billGroupRepository.save(billGroup);
        return mapToResponse(savedGroup);
    }

    @Transactional(readOnly = true)
    public BillGroupResponse getGroupById(String groupId) {
        BillGroup billGroup = billGroupRepository.findWithParticipantsById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + groupId));

        return mapToResponse(billGroup);
    }

    private BillGroupResponse mapToResponse(BillGroup group) {
        List<ParticipantResponse> participants = group.getParticipants().stream()
                .map(p -> ParticipantResponse.builder()
                        .participantId(p.getId())
                        .name(p.getName())
                        .build())
                .collect(Collectors.toList());

        return BillGroupResponse.builder()
                .groupId(group.getId())
                .name(group.getName())
                .participants(participants)
                .createdAt(group.getCreatedAt())
                .build();
    }
}
