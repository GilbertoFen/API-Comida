package com.demoapi.apicomida.dtos;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import java.util.UUID;

public final class AiDtos {

    private AiDtos() {
    }

    public record EstimateCaloriesRequest(@NotBlank String prompt) {
    }

    public record GenericAiRequest(Map<String, Object> input) {
    }

    public record AiResponse(
            UUID id,
            String requestType,
            String status,
            Map<String, Object> outputData
    ) {
    }
}
