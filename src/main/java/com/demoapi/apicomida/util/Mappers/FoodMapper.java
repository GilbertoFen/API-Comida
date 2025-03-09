package com.demoapi.apicomida.util.Mappers;

import com.demoapi.apicomida.models.DTO.FoodDTO;
import com.demoapi.apicomida.models.FoodModel;

public class FoodMapper {
    public static FoodModel toModel(FoodDTO foodDTO) {
        return FoodModel.builder()
                .id(foodDTO.getId())
                .country(foodDTO.getCountry())
                .category(foodDTO.getCategory())
                .name(foodDTO.getName())
                .quantity(foodDTO.getQuantity())
                .unit(foodDTO.getUnit())
                .calories(foodDTO.getCalories())
                .protein(foodDTO.getProtein())
                .carb(foodDTO.getCarb())
                .fat(foodDTO.getFat())
                .sugar(foodDTO.getSugar())
                .sodium(foodDTO.getSodium())
                .build();
    }

    public static FoodDTO toDTO(FoodModel foodModel) {
        return FoodDTO.builder()
                .id(foodModel.getId())
                .country(foodModel.getCountry())
                .category(foodModel.getCategory())
                .name(foodModel.getName())
                .quantity(foodModel.getQuantity())
                .unit(foodModel.getUnit())
                .calories(foodModel.getCalories())
                .protein(foodModel.getProtein())
                .carb(foodModel.getCarb())
                .fat(foodModel.getFat())
                .sugar(foodModel.getSugar())
                .sodium(foodModel.getSodium())
                .build();
    }
}
