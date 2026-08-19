package com.allobank.splitbill.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ParticipantResponse {

    private String participantId;
    private String name;

    public ParticipantResponse() {
    }

    public ParticipantResponse(String participantId, String name) {
        this.participantId = participantId;
        this.name = name;
    }

    public static ParticipantResponseBuilder builder() {
        return new ParticipantResponseBuilder();
    }

    public static class ParticipantResponseBuilder {
        private String participantId;
        private String name;

        public ParticipantResponseBuilder participantId(String participantId) {
            this.participantId = participantId;
            return this;
        }

        public ParticipantResponseBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ParticipantResponse build() {
            return new ParticipantResponse(participantId, name);
        }
    }

    public String getParticipantId() {
        return participantId;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
