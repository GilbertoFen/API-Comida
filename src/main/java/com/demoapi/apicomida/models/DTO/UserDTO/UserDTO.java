package com.demoapi.apicomida.models.DTO.UserDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class UserDTO {
        @JsonProperty
        private String name;
        @JsonProperty
        private int age;
        @JsonProperty
        private String email;
        @JsonProperty
        private float weight;
        @JsonProperty
        private float height;
        @JsonProperty
        private int exerciseLevel;
}
