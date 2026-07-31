package com.nonmus.nonmus.modules.user.controller;

import com.nonmus.nonmus.modules.common.exception.UserNotFoundException;
import com.nonmus.nonmus.modules.common.util.AuthUtil;
import com.nonmus.nonmus.modules.user.dto.request.UpdateUserRequest;
import com.nonmus.nonmus.modules.user.dto.response.UserResponse;
import com.nonmus.nonmus.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import com.nonmus.nonmus.modules.user.dto.request.UserCreateRequest;
import com.nonmus.nonmus.modules.user.entity.Users;
import com.nonmus.nonmus.modules.user.service.UsersService;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UsersService usersService;
    private final ModelMapper modelMapper;

    @PostMapping
    public UserResponse createUser(@RequestBody UserCreateRequest request) {
        Users user = usersService.createUser(request);
        return modelMapper.map(user, UserResponse.class);
    }

    @GetMapping("/me")
    public UserResponse getLoggedInUser() {
        AuthenticatedUser authenticatedUser= AuthUtil.getPrincipal();
        String email = authenticatedUser.getEmail();

        Users user = usersService.getUsersByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));
        return modelMapper.map(user, UserResponse.class);
    }

    @PutMapping
    public UserResponse updateUser(@RequestBody UpdateUserRequest request) {
        AuthenticatedUser authenticatedUser = AuthUtil.getPrincipal();
        String email = authenticatedUser.getEmail();

        Users user = usersService.updateUser(email, request);
        return modelMapper.map(user, UserResponse.class);
    }
}
