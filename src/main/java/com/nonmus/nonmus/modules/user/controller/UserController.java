package com.nonmus.nonmus.modules.user.controller;

import java.util.UUID;

import com.nonmus.nonmus.modules.common.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nonmus.nonmus.modules.user.dto.request.UserCreateRequest;
import com.nonmus.nonmus.modules.user.entity.Users;
import com.nonmus.nonmus.modules.user.service.UsersService;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UsersService usersService;

    @PostMapping
    public Users createUser(@RequestBody UserCreateRequest request) {
        return usersService.createUser(request);
    }

    @GetMapping("/me")
    public Users getLoggedInUser() {
        String email = AuthUtil.getPrincipal();
        return usersService.getUserByEmail(email);
    }
}
