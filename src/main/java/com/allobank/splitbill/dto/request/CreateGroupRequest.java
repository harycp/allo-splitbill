package com.allobank.splitbill.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateGroupRequest {

    @NotBlank(message = "Group name must not be blank")
    private String name;

    @NotEmpty(message = "Participants list must not be empty")
    @Valid
    private List<ParticipantRequest> participants;

    public CreateGroupRequest() {
    }

    public CreateGroupRequest(String name, List<ParticipantRequest> participants) {
        this.name = name;
        this.participants = participants;
    }

    public static CreateGroupRequestBuilder builder() {
        return new CreateGroupRequestBuilder();
    }

    public static class CreateGroupRequestBuilder {
        private String name;
        private List<ParticipantRequest> participants;

        public CreateGroupRequestBuilder name(String name) {
            this.name = name;
            return this;
        }

        public CreateGroupRequestBuilder participants(List<ParticipantRequest> participants) {
            this.participants = participants;
            return this;
        }

        public CreateGroupRequest build() {
            return new CreateGroupRequest(name, participants);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ParticipantRequest> getParticipants() {
        return participants;
    }

    public void setParticipants(List<ParticipantRequest> participants) {
        this.participants = participants;
    }
}
