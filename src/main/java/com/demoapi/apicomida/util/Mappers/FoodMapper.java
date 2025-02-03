package com.demoapi.apicomida.util.Mappers;

import com.demoapi.apicomida.models.DTO.FoodDTO;
import com.demoapi.apicomida.models.FoodModel;

public class FoodMapper {
    public static FoodModel toModel(FoodDTO foodDTO) {
        return FoodModel.builder()
                .name(foodDTO.getName())
                .calories(foodDTO.getCalories())
                .sugar(foodDTO.getSugar())
                .protein(foodDTO.getProtein())
                .fat(foodDTO.getFat())
                .carb(foodDTO.getCarb())
                .build();
    }

    public static FoodDTO toDTO(FoodModel foodModel) {
        return FoodDTO.builder()
                .name(foodModel.getName())
                .calories(foodModel.getCalories())
                .sugar(foodModel.getSugar())
                .protein(foodModel.getProtein())
                .fat(foodModel.getFat())
                .carb(foodModel.getCarb())
                .build();
    }
}
