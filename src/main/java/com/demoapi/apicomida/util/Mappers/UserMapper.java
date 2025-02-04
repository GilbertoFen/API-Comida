package com.demoapi.apicomida.util.Mappers;

import com.demoapi.apicomida.models.DTO.UserDTO.User;
import com.demoapi.apicomida.models.DTO.UserDTO.UserDTO;
import com.demoapi.apicomida.models.UserModel;

public class UserMapper {
    public static UserModel toModel(User userDTO) {
        return UserModel.builder()
                .name(userDTO.getName())
                .age(userDTO.getAge())
                .email(userDTO.getEmail())
                .password(userDTO.getPassword())
                .weight(userDTO.getWeight())
                .height(userDTO.getHeight())
                .exerciseLevel(userDTO.getExerciseLevel())
                .build();
    }

    public static User toDTO(UserModel userModel) {
        return User.builder()
                .name(userModel.getName())
                .age(userModel.getAge())
                .email(userModel.getEmail())
                .weight(userModel.getWeight())
                .height(userModel.getHeight())
                .exerciseLevel(userModel.getExerciseLevel())
                .build();
    }

    public static UserDTO toUserDTO(UserModel userModel) {
        return UserDTO.builder()
                .name(userModel.getName())
                .age(userModel.getAge())
                .email(userModel.getEmail())
                .weight(userModel.getWeight())
                .height(userModel.getHeight())
                .exerciseLevel(userModel.getExerciseLevel())
                .build();
    }
}

