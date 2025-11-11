package com.nonmus.auth_service.controller;

import com.nonmus.auth_service.dto.LoginRequest;
import com.nonmus.auth_service.dto.LoginResponse;
import com.nonmus.auth_service.dto.Status;
import com.nonmus.auth_service.entity.User;
import com.nonmus.auth_service.service.UserLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class LoginController {

    private final UserLoginService userLoginService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        // 1. The service will return a User on success or throw an exception on failure.
        User authenticatedUser = userLoginService.authenticate(request);

        // 2. Generate real tokens using the authenticated user object.
        String accessToken = "accessToken"; // jwtService.generateToken(authenticatedUser);
        String refreshToken = "refreshToken"; // jwtService.generateRefreshToken(authenticatedUser); // Example method

        // 3. Build the success response. The failure cases are handled by GlobalExceptionHandler.
        LoginResponse response = new LoginResponse();
        response.setStatus(Status.SUCCESS);
        response.setMessage("Login Verification Successful");
        response.setToken(accessToken);
        response.setRefreshToken(refreshToken);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
