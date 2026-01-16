package com.nonmus.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.nonmus.dto.UserValidateRequest;
import com.nonmus.dto.UserValidateResponse;

@FeignClient(name = "user-service", url = "http://localhost:8001")
public interface UserServiceClient {
    @PostMapping("/api/v1/users/validate")
    public UserValidateResponse validate(@RequestBody UserValidateRequest request);
}
