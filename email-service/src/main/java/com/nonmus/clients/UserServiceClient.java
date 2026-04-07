package com.nonmus.clients;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.nonmus.decoder.UserServiceFeignErrorDecoder;
import com.nonmus.dto.UserData;
import com.nonmus.dto.UserValidateRequest;
import com.nonmus.dto.UserValidateResponse;

@FeignClient(
    name = "user-service", 
    url = "http://localhost:8001",
    configuration = UserServiceFeignErrorDecoder.class)
public interface UserServiceClient {
    @PostMapping("/api/v1/users/validate")
    public UserValidateResponse validate(@RequestBody UserValidateRequest request);

    @GetMapping("/api/v1/users")
    public UserData getUserDataByEmail(@RequestParam("email") String email);

    @GetMapping("/api/v1/users")
    public UserData getUserDataByUserId(@RequestParam("userId") UUID userId);
}
