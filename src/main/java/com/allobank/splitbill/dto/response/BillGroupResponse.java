package com.allobank.splitbill.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BillGroupResponse {

    private String groupId;
    private String name;
    private List<ParticipantResponse> participants;
    private LocalDateTime createdAt;

    public BillGroupResponse() {
    }

    public BillGroupResponse(String groupId, String name, List<ParticipantResponse> participants, LocalDateTime createdAt) {
        this.groupId = groupId;
        this.name = name;
        this.participants = participants;
        this.createdAt = createdAt;
    }

    public static BillGroupResponseBuilder builder() {
        return new BillGroupResponseBuilder();
    }

    public static class BillGroupResponseBuilder {
        private String groupId;
        private String name;
        private List<ParticipantResponse> participants;
        private LocalDateTime createdAt;

        public BillGroupResponseBuilder groupId(String groupId) {
            this.groupId = groupId;
            return this;
        }

        public BillGroupResponseBuilder name(String name) {
            this.name = name;
            return this;
        }

        public BillGroupResponseBuilder participants(List<ParticipantResponse> participants) {
            this.participants = participants;
            return this;
        }

        public BillGroupResponseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public BillGroupResponse build() {
            return new BillGroupResponse(groupId, name, participants, createdAt);
        }
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ParticipantResponse> getParticipants() {
        return participants;
    }

    public void setParticipants(List<ParticipantResponse> participants) {
        this.participants = participants;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
