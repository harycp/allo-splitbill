package com.allobank.splitbill.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ParticipantRequest {

    @NotBlank(message = "Participant name must not be blank")
    private String name;

    public ParticipantRequest() {
    }

    public ParticipantRequest(String name) {
        this.name = name;
    }

    public static ParticipantRequestBuilder builder() {
        return new ParticipantRequestBuilder();
    }

    public static class ParticipantRequestBuilder {
        private String name;

        public ParticipantRequestBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ParticipantRequest build() {
            return new ParticipantRequest(name);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
