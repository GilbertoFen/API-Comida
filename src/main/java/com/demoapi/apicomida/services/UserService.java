package com.demoapi.apicomida.services;

import com.demoapi.apicomida.models.DTO.UserDTO.User;
import com.demoapi.apicomida.models.UserModel;
import com.demoapi.apicomida.repositories.UserRepository;
import com.demoapi.apicomida.util.Mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.util.List;

import static com.demoapi.apicomida.util.Mappers.UserMapper.toModel;

@Service
public class UserService {
    UserRepository userRepository;
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public ResponseEntity<?> createUser(User user) {
        try{
            userRepository.save(toModel(user));
            return ResponseEntity.status(HttpStatusCode.valueOf(201)).build();
        }catch(Exception e){
            return ResponseEntity.status(HttpStatusCode.valueOf(500)).body("Error al crear usuario");
        }
    }
    public List<UserModel> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(long id) {
        UserModel userModel = userRepository.findById(id);
        return UserMapper.toDTO(userModel);
    }

    public ResponseEntity<?> updateUser(long id, User userDTO) {
        try {
            if (!userRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
            }
            UserModel userModel = toModel(userDTO);
            userModel.setIdUser(id);
            userRepository.save(userModel);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar usuario");
        }
    }

    public ResponseEntity<?> deleteUser(long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
    }

}
