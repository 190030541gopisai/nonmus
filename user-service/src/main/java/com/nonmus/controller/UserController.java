package com.nonmus.controller;

import org.springframework.web.bind.annotation.RestController;

import com.nonmus.dto.ApiResponse;
import com.nonmus.dto.Meta;
import com.nonmus.dto.UserCreateRequest;
import com.nonmus.dto.UserCreateResponse;
import com.nonmus.dto.UserData;
import com.nonmus.dto.UserValidateRequest;
import com.nonmus.dto.UserValidateResponse;
import com.nonmus.entity.User;
import com.nonmus.service.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestMapping;

import java.time.Instant;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;





@RestController
@RequestMapping("${api.prefix}")
@Validated
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserCreateResponse>> createUser(@Valid @RequestBody UserCreateRequest request) {
        User user = userService.createUser(request);

        ApiResponse<UserCreateResponse> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setStatusCode(201);
        response.setMessage("User Creation Successful");
        
        UserCreateResponse userCreateResponse = new UserCreateResponse();
        userCreateResponse.setUserId(user.getUserId());
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

    @PostMapping("/validate")
    public UserValidateResponse validate(@Valid @RequestBody UserValidateRequest request) {
        UserValidateResponse userValidateResponse = userService.validate(request);
        return userValidateResponse;
    }
    
    @GetMapping(params = {"userId"})
    public UserData getUserData(@RequestParam("userId") UUID userId) {
        User user = userService.getUserData(userId);
        return mapToUserData(user);
    }

    @GetMapping(params = {"email"})
    public UserData getUserData(@RequestParam("email") String email) {
        User user = userService.getUserData(email);

        return mapToUserData(user);
    }

    @PutMapping("/email/verified/{id}")
    public UserData updateEmailVerified(@PathVariable("id") UUID userId) {
        User user = userService.updateEmailVerified(userId);
        return mapToUserData(user);
    }

    private UserData mapToUserData(User user) {
        if(user == null) {
            return null;
        }
    
        UserData userData = new UserData();
        userData.setUserId(user.getUserId());
        userData.setEmail(user.getEmail());
        userData.setFirstName(user.getFirstName());
        userData.setLastName(user.getLastName());
        userData.setEmailVerified(user.isEmailVerified());
        
        return userData;
    }
}
