package com.nonmus.nonmus.modules.user.controller;

import com.nonmus.nonmus.modules.common.exception.UserNotFoundException;
import com.nonmus.nonmus.modules.common.util.AuthUtil;
import com.nonmus.nonmus.modules.common.util.JwtUtil;
import com.nonmus.nonmus.modules.user.dto.request.UserCreateRequest;
import com.nonmus.nonmus.modules.user.entity.Users;
import com.nonmus.nonmus.modules.user.service.UsersService;
import com.nonmus.nonmus.security.AuthenticatedUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.Optional;

import static org.mockito.Mockito.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @MockitoBean
    private UsersService usersService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private HttpSecurity security;

    @Autowired
    private MockMvc mockMvc;

    private RestTestClient client;

    @BeforeEach
    void setup() {
        client = RestTestClient.bindTo(mockMvc).build();
    }

    @Test
    void createUser_shouldReturnCreatedUser() {
        UserCreateRequest request = new UserCreateRequest();
        request.setName("Test");
        request.setEmail("test@test.com");

        Users user = new Users();
        user.setEmail("test@test.com");

        when(usersService.createUser(request)).thenReturn(user);

        client.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.email").isEqualTo("test@test.com");
    }

    @Test
    void getLoggedInUser_shouldReturnUser() {
        AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setEmail("user@test.com");

        Users user = new Users();
        user.setEmail("user@test.com");

        try (MockedStatic<AuthUtil> authUtil = mockStatic(AuthUtil.class)) {
            authUtil.when(AuthUtil::getPrincipal).thenReturn(authenticatedUser);
            when(usersService.getUsersByEmail("user@test.com")).thenReturn(Optional.of(user));

            client.get()
                    .uri("/api/v1/users/me")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.email").isEqualTo("user@test.com");
        }
    }

    @Test
    void getLoggedInUser_shouldThrowNotFound_whenUserDoesNotExist() {
        AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setEmail("unknown@test.com");

        try (MockedStatic<AuthUtil> authUtil = mockStatic(AuthUtil.class)) {
            authUtil.when(AuthUtil::getPrincipal).thenReturn(authenticatedUser);
            when(usersService.getUsersByEmail("unknown@test.com")).thenReturn(Optional.empty());

            client.get()
                    .uri("/api/v1/users/me")
                    .exchange()
                    .expectStatus().isNotFound();
        }
    }
}