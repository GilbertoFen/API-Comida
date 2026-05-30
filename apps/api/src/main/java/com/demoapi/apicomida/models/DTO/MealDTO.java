package com.demoapi.apicomida.models.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

public class MealDTO {
    private Long id;

    @NotNull
    @Positive
    private Long idUser;

    @NotEmpty
    private List<Long> recipeIds;

    @NotBlank
    private String date;

    private Double totalCalories;

    public MealDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public List<Long> getRecipeIds() {
        return recipeIds;
    }

    public void setRecipeIds(List<Long> recipeIds) {
        this.recipeIds = recipeIds;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(Double totalCalories) {
        this.totalCalories = totalCalories;
    }
}
