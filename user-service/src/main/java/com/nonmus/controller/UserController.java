package com.nonmus.controller;

import org.springframework.web.bind.annotation.RestController;

import com.nonmus.dto.ApiResponse;
import com.nonmus.dto.Meta;
import com.nonmus.dto.UserCreateRequest;
import com.nonmus.dto.UserCreateResponse;
import com.nonmus.entity.User;
import com.nonmus.service.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestMapping;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("${api.prefix}")
@Validated
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createUser(@Valid @RequestBody UserCreateRequest request) {
        User user = userService.createUser(request);

        ApiResponse<UserCreateResponse> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setStatusCode(201);
        response.setMessage("User Creation Successful");
        
        UserCreateResponse userCreateResponse = new UserCreateResponse();
        userCreateResponse.setUserId(user.getUserId().toString());
        userCreateResponse.setEmail(user.getEmail());
        userCreateResponse.setFirstName(user.getFirstName());
        userCreateResponse.setLastName(user.getLastName());
        userCreateResponse.setEmailVerified(user.isEmailVerified());

        response.setData(userCreateResponse);

        Meta meta = new Meta();
        meta.setTimeStamp(Instant.now());

        response.setMeta(meta);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
