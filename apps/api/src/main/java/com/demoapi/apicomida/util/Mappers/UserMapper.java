package com.demoapi.apicomida.util.Mappers;

import com.demoapi.apicomida.models.DTO.UserDTO.User;
import com.demoapi.apicomida.models.DTO.UserDTO.UserDTO;
import com.demoapi.apicomida.models.UserModel;

public class UserMapper {

    private UserMapper() {
    }

    public static UserModel toModel(User userDTO) {
        UserModel userModel = new UserModel();
        userModel.setName(userDTO.getName());
        userModel.setAge(userDTO.getAge());
        userModel.setEmail(userDTO.getEmail());
        userModel.setPassword(userDTO.getPassword());
        userModel.setWeight(userDTO.getWeight());
        userModel.setHeight(userDTO.getHeight());
        userModel.setExerciseLevel(userDTO.getExerciseLevel());
        return userModel;
    }

    public static UserDTO toUserDTO(UserModel userModel) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(userModel.getIdUser());
        userDTO.setName(userModel.getName());
        userDTO.setAge(userModel.getAge());
        userDTO.setEmail(userModel.getEmail());
        userDTO.setWeight(userModel.getWeight());
        userDTO.setHeight(userModel.getHeight());
        userDTO.setExerciseLevel(userModel.getExerciseLevel());
        return userDTO;
    }
}
