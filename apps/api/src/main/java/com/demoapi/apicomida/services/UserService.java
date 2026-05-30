package com.demoapi.apicomida.services;

import com.demoapi.apicomida.models.DTO.UserDTO.User;
import com.demoapi.apicomida.models.DTO.UserDTO.UserDTO;
import com.demoapi.apicomida.models.UserModel;
import com.demoapi.apicomida.repositories.UserRepository;
import com.demoapi.apicomida.util.Mappers.UserMapper;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDTO createUser(User user) {
        if (userRepository.existsByEmailIgnoreCase(user.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        UserModel userModel = UserMapper.toModel(user);
        return UserMapper.toUserDTO(userRepository.save(userModel));
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(UserMapper::toUserDTO).toList();
    }

    public UserDTO getUserById(long id) {
        UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return UserMapper.toUserDTO(user);
    }

    public UserDTO updateUser(long id, User userDTO) {
        UserModel existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!existingUser.getEmail().equalsIgnoreCase(userDTO.getEmail())
                && userRepository.existsByEmailIgnoreCase(userDTO.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        existingUser.setName(userDTO.getName());
        existingUser.setAge(userDTO.getAge());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setPassword(userDTO.getPassword());
        existingUser.setWeight(userDTO.getWeight());
        existingUser.setHeight(userDTO.getHeight());
        existingUser.setExerciseLevel(userDTO.getExerciseLevel());

        return UserMapper.toUserDTO(userRepository.save(existingUser));
    }

    public void deleteUser(long id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        userRepository.deleteById(id);
    }
}
