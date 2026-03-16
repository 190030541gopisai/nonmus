package com.nonmus.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.nonmus.config.UserServiceFeignConfig;
import com.nonmus.dto.ApiResponse;
import com.nonmus.dto.UserCreateRequest;
import com.nonmus.dto.UserCreateResponse;

@FeignClient(
    name = "user-service",
    url = "${services.user.url}",
    configuration = UserServiceFeignConfig.class)
public interface UserServiceClient {

    @PostMapping("/api/v1/users")
    ApiResponse<UserCreateResponse> createUser(@RequestBody UserCreateRequest request);
}
