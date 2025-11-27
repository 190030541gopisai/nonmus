package com.nonmus.user_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nonmus.user_service.dto.UserRequest;
import com.nonmus.user_service.dto.UserResponse;
import com.nonmus.user_service.entity.User;
import com.nonmus.user_service.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;

@Validated
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUserProfile(@Valid @RequestBody UserRequest userRequest) {
        User user = userService.createUserProfile(userRequest);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        UserResponse userResponse = new UserResponse();
        userResponse.setUserId(user.getId().toString());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setEmail(user.getEmail());
        userResponse.setEmailVerified(user.isEmailVerified());
        userResponse.setCreatedAt(user.getCreatedAt().toString());

        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserProfileByEmail(@PathVariable("email") String email) {
        User user = userService.getUserByEmail(email);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        UserResponse userResponse = new UserResponse();
        userResponse.setUserId(user.getId().toString());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setEmail(user.getEmail());
        userResponse.setEmailVerified(user.isEmailVerified());
        userResponse.setCreatedAt(user.getCreatedAt().toString());

        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @PutMapping("/activate/{email}")
    public ResponseEntity<String> activateUserEmail(@PathVariable("email") String email) {
        userService.activateUserEmail(email);

        return new ResponseEntity<>("Email verified successfully", HttpStatus.OK);
    }
}
