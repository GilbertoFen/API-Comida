package com.demoapi.apicomida.util.Mappers;

import com.demoapi.apicomida.models.DTO.MealDTO;
import com.demoapi.apicomida.models.MealModel;

public class MealMapper {
    public static MealModel toModel(MealDTO mealDTO) {
        return MealModel.builder()
                .date(mealDTO.getDate())
                .totalCalories(mealDTO.getTotalCalories())
                .build();
    }

    public static MealDTO toDTO(MealModel mealModel) {
        return MealDTO.builder()
                .date(mealModel.getDate())
                .totalCalories(mealModel.getTotalCalories())
                .build();
    }
}
