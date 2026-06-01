package com.demoapi.apicomida.controllers;

import com.demoapi.apicomida.dtos.UserDtos.UpdateProfileRequest;
import com.demoapi.apicomida.services.UserService;
import com.demoapi.apicomida.util.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/me")
    public Object getMe() {
        return userService.getMe();
    }

    @PatchMapping("/users/me")
    public Object updateMe(@Valid @RequestBody UpdateProfileRequest request) {
        return userService.updateMe(request);
    }

    @DeleteMapping("/users/me")
    public Object deleteMe() {
        userService.deleteMe();
        return ApiResponses.message("User deactivated");
    }

    @GetMapping("/users/me/stats")
    public Object getStats() {
        return userService.getStats();
    }
}
