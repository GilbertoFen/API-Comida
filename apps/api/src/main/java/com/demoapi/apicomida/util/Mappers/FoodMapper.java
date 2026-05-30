package com.demoapi.apicomida.util.Mappers;

import com.demoapi.apicomida.models.DTO.FoodDTO;
import com.demoapi.apicomida.models.FoodModel;

public class FoodMapper {
    private FoodMapper() {
    }

    public static FoodModel toModel(FoodDTO foodDTO) {
        FoodModel foodModel = new FoodModel();
        foodModel.setId(foodDTO.getId());
        foodModel.setCountry(foodDTO.getCountry());
        foodModel.setCategory(foodDTO.getCategory());
        foodModel.setName(foodDTO.getName());
        foodModel.setQuantity(foodDTO.getQuantity());
        foodModel.setUnit(foodDTO.getUnit());
        foodModel.setCalories(foodDTO.getCalories());
        foodModel.setProtein(foodDTO.getProtein());
        foodModel.setCarb(foodDTO.getCarb());
        foodModel.setFat(foodDTO.getFat());
        foodModel.setSugar(foodDTO.getSugar());
        foodModel.setSodium(foodDTO.getSodium());
        return foodModel;
    }

    public static FoodDTO toDTO(FoodModel foodModel) {
        FoodDTO foodDTO = new FoodDTO();
        foodDTO.setId(foodModel.getId());
        foodDTO.setCountry(foodModel.getCountry());
        foodDTO.setCategory(foodModel.getCategory());
        foodDTO.setName(foodModel.getName());
        foodDTO.setQuantity(foodModel.getQuantity());
        foodDTO.setUnit(foodModel.getUnit());
        foodDTO.setCalories(foodModel.getCalories());
        foodDTO.setProtein(foodModel.getProtein());
        foodDTO.setCarb(foodModel.getCarb());
        foodDTO.setFat(foodModel.getFat());
        foodDTO.setSugar(foodModel.getSugar());
        foodDTO.setSodium(foodModel.getSodium());
        return foodDTO;
    }
}
