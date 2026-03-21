package com.nonmus.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.nonmus.decoder.UserServiceFeignErrorDecoder;
import com.nonmus.dto.ApiResponse;
import com.nonmus.dto.UserCreateRequest;
import com.nonmus.dto.UserCreateResponse;
import com.nonmus.dto.UserData;

@FeignClient(
    name = "user-service",
    url = "${services.user.url}",
    configuration = UserServiceFeignErrorDecoder.class)
public interface UserServiceClient {

    @PostMapping("/api/v1/users")
    ApiResponse<UserCreateResponse> createUser(@RequestBody UserCreateRequest request);

    @PutMapping("/email/verified/{id}")
    UserData updateEmailVerified(@PathVariable("id") UUID userId); 
}
