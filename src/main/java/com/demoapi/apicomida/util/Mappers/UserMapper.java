package com.demoapi.apicomida.util.Mappers;

import com.demoapi.apicomida.models.DTO.UserDTO.User;
import com.demoapi.apicomida.models.UserModel;
import com.demoapi.apicomida.util.Exercise;

public class UserMapper {

    public static UserModel toModel(User userDTO) {
        return UserModel.builder()
                .name(userDTO.getName())
                .age(userDTO.getAge())
                .email(userDTO.getEmail())
                .password(userDTO.getPassword())
                .weight(userDTO.getWeight())
                .height(userDTO.getHeight())
                .exerciseLevel(Exercise.valueOf(userDTO.getExerciseLevel().toUpperCase()).getId())
                .build();
    }

    public static User toDTO(UserModel userModel) {
        return User.builder()
                .name(userModel.getName())
                .age(userModel.getAge())
                .email(userModel.getEmail())
                .weight(userModel.getWeight())
                .height(userModel.getHeight())
                .exerciseLevel(Exercise.getById(userModel.getExerciseLevel()).getName())
                .build();
    }
}
