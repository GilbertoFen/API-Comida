package com.demoapi.apicomida.services;

import com.demoapi.apicomida.models.DTO.UserDTO.User;
import com.demoapi.apicomida.models.DTO.UserDTO.UserDTO;
import com.demoapi.apicomida.models.UserModel;
import com.demoapi.apicomida.repositories.UserRepository;
import com.demoapi.apicomida.util.Mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.demoapi.apicomida.util.Mappers.UserMapper.toModel;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResponseEntity<String> createUser(User user) {
        try {
            UserModel userModel = toModel(user);
            userRepository.save(userModel);
            return ResponseEntity.status(HttpStatus.CREATED).body("User created succesfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating user");
        }
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(UserMapper::toUserDTO).toList();
    }

    public ResponseEntity<UserDTO> getUserById(long id) {
        Optional<UserModel> userModelOptional = userRepository.findById(id);
        return userModelOptional
                .map(user -> ResponseEntity.ok(UserMapper.toUserDTO(user)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }


    public ResponseEntity<String> updateUser(long id, User userDTO) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        try {
            UserModel userModel = toModel(userDTO);
            userModel.setIdUser(id);
            userRepository.save(userModel);
            return ResponseEntity.ok("User updated succesfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating user");
        }
    }

    public ResponseEntity<String> deleteUser(long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.ok("User deleted succesfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }
}
