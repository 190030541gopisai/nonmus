package com.nonmus.auth_service.controller;

import com.nonmus.auth_service.config.jwt.JwtService;
import com.nonmus.auth_service.constants.Constants;
import com.nonmus.auth_service.dto.LoginRequest;
import com.nonmus.auth_service.dto.LoginResponse;
import com.nonmus.auth_service.entity.User;
import com.nonmus.auth_service.service.UserLoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class LoginController {

    private final UserLoginService userLoginService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        User authenticatedUser = userLoginService.authenticate(request);

        if(authenticatedUser == null) {
            // This case should ideally never happen due to exceptions thrown in the service layer.
            LoginResponse response = new LoginResponse();
            response.setStatus("LOGIN_FAILED");
            response.setMessage("Login failed due to unknown reasons.");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }


        String accessToken = jwtService.generateToken(authenticatedUser.getEmail());
        String refreshToken = jwtService.generateRefreshToken(authenticatedUser.getEmail());

        LoginResponse response = new LoginResponse();
        response.setStatus(Constants.SUCCESS);
        response.setMessage("Login Verification Successful");
        response.setToken(accessToken);
        response.setRefreshToken(refreshToken);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
