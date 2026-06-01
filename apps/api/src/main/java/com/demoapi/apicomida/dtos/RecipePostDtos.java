package com.demoapi.apicomida.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public final class RecipePostDtos {

    private RecipePostDtos() {
    }

    public record RecipePostRequest(
            @NotNull UUID recipeId,
            @NotBlank String title,
            String content
    ) {
    }

    public record RecipePostResponse(
            UUID id,
            UUID recipeId,
            UUID userId,
            String title,
            String content,
            int likesCount,
            int commentsCount
    ) {
    }
}
