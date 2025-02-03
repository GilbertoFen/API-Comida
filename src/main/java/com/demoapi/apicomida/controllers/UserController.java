package com.demoapi.apicomida.controllers;

import com.demoapi.apicomida.models.DTO.UserDTO.User;
import com.demoapi.apicomida.models.DTO.UserDTO.UserDTO;
import com.demoapi.apicomida.models.UserModel;
import com.demoapi.apicomida.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/")
    public ResponseEntity<List<UserModel>> getAllUsers() {
        List<UserModel> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<String> registerUser(@RequestBody User userDTO) {
        try {
            userService.createUser(userDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("Usuario creado exitosamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear el usuario.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable long id) {
        User user = userService.getUserById(id);
        if (user !=  null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable long id, @RequestBody User userDTO) {
        return userService.updateUser(id, userDTO);
    }
    @PutMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable long id){
        return userService.deleteUser(id);
    }
}
