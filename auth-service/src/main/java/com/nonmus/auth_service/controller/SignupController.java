package com.nonmus.auth_service.controller;

import com.nonmus.auth_service.dto.RegistrationRequest;
import com.nonmus.auth_service.dto.Status;
import com.nonmus.auth_service.dto.UserRegistrationResponse;
import com.nonmus.auth_service.service.UserRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/")
@RequiredArgsConstructor
public class SignupController {

    private final UserRegistrationService userRegistrationService;

    @PostMapping("/register")
    public ResponseEntity<UserRegistrationResponse> register(@RequestBody RegistrationRequest request) {
        userRegistrationService.register(request);

        UserRegistrationResponse response = new UserRegistrationResponse();
        response.setStatus(Status.SUCCESS);
        response.setMessage("User Registered Successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
