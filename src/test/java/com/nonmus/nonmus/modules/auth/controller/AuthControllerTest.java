package com.nonmus.nonmus.modules.auth.controller;

import com.nonmus.nonmus.modules.auth.dto.internal.LoginResult;
import com.nonmus.nonmus.modules.auth.dto.internal.SignUpResult;
import com.nonmus.nonmus.modules.auth.dto.internal.TokenPair;
import com.nonmus.nonmus.modules.auth.dto.request.LoginRequest;
import com.nonmus.nonmus.modules.auth.dto.request.SignUpRequest;
import com.nonmus.nonmus.modules.auth.service.AuthenticationService;
import com.nonmus.nonmus.modules.auth.service.JwtCookieService;
import com.nonmus.nonmus.modules.auth.service.RefreshTokenService;
import com.nonmus.nonmus.modules.auth.service.UserRegistrationService;
import com.nonmus.nonmus.modules.common.util.JwtUtil;
import com.nonmus.nonmus.modules.user.entity.Users;
import com.nonmus.nonmus.modules.user.service.UsersService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {
    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private JwtCookieService jwtCookieService;

    @MockitoBean
    private RefreshTokenService refreshTokenService;

    @MockitoBean
    private UserRegistrationService userRegistrationService;

    @MockitoBean
    private AuthenticationService authService;

    @MockitoBean
    private UsersService usersService;

    @MockitoBean
    private HttpSecurity httpSecurity;

    @Autowired
    private MockMvc mockMvc;

    private RestTestClient client;

    @BeforeEach
    public void setup() {
        client = RestTestClient.bindTo(mockMvc).build();
    }

    @Test
    void signup_shouldReturnCreated() throws Exception {
        SignUpRequest request = new SignUpRequest();
        request.setName("abc");
        request.setEmail("abc@test.com");
        request.setPassword("password");

        Users user = new Users();
        user.setEmail("abc@test.com");

        TokenPair tokenPair = new TokenPair("access","refresh");

        SignUpResult result = new SignUpResult(user, tokenPair);

        when(userRegistrationService.signup(any()))
                .thenReturn(result);

        client.post().uri("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Signup successful");
    }

    @Test
    void login_shouldReturnSuccess_whenCredentialsAreCorrect() {
        LoginRequest request = new LoginRequest();
        request.setEmail("abc@test.com");
        request.setPassword("password");

        Users user = new Users();
        user.setEmail("abc@test.com");

        TokenPair tokenPair = new TokenPair("access","refresh");

        LoginResult loginResult = new LoginResult(user, tokenPair);

        when(authService.login(
                "abc@test.com",
                "password",
                false))
                .thenReturn(loginResult);

        client.post().uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .json(
                        """
                                {   
                                    "message": "Login Successful"
                                }
                                """
                );

        verify(authService)
                .login(request.getEmail(), request.getPassword(), request.isRememberMe());

        verify(jwtCookieService)
                .addJwtCookies(eq(tokenPair), any());
    }

    @Test
    void refresh_shouldReturnOk() {

        client.post()
                .uri("/api/v1/auth/refresh")
                .exchange()
                .expectStatus().isOk();

        verify(refreshTokenService)
                .refreshAccessToken(any(HttpServletRequest.class), any(HttpServletResponse.class));
    }

    @Test
    void logout_shouldReturnOk() {
        client.post()
                .uri("/api/v1/auth/logout")
                .exchange()
                .expectStatus()
                .isOk();

        verify(authService)
                .logout(any());
    }
}