package com.demoapi.apicomida.dtos;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public final class ExerciseDtos {

    private ExerciseDtos() {
    }

    public record ExerciseRequest(
            @NotBlank String name,
            String muscleGroup,
            String equipment,
            String difficulty,
            String description,
            Integer recommendedSets,
            String recommendedReps,
            Integer recommendedTimeSeconds
    ) {
    }

    public record ExerciseResponse(
            UUID id,
            String name,
            String muscleGroup,
            String equipment,
            String difficulty,
            String description,
            Integer recommendedSets,
            String recommendedReps,
            Integer recommendedTimeSeconds
    ) {
    }
}
