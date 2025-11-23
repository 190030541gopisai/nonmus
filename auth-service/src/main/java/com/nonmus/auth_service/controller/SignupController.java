package com.nonmus.auth_service.controller;

import com.nonmus.auth_service.constants.Constants;
import com.nonmus.auth_service.dto.RegistrationRequest;
import com.nonmus.auth_service.dto.UserRegistrationResponse;
import com.nonmus.auth_service.service.UserRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/auth/")
@RequiredArgsConstructor
public class SignupController {

    private final UserRegistrationService userRegistrationService;

    @PostMapping("/register")
    public ResponseEntity<UserRegistrationResponse> register(@Valid @RequestBody RegistrationRequest request) {
        userRegistrationService.register(request);

        log.info("User registered with email: " + request.getEmail());
        UserRegistrationResponse response = new UserRegistrationResponse();
        response.setStatus(Constants.SUCCESS);
        response.setMessage("User Registered Successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
