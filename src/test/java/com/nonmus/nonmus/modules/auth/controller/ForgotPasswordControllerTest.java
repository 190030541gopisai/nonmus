package com.nonmus.nonmus.modules.auth.controller;

import com.nonmus.nonmus.modules.auth.dto.request.ForgotPasswordRequest;
import com.nonmus.nonmus.modules.auth.dto.request.ForgotPasswordVerifyRequest;
import com.nonmus.nonmus.modules.auth.dto.request.ResetPasswordRequest;
import com.nonmus.nonmus.modules.auth.service.AuthenticationService;
import com.nonmus.nonmus.modules.auth.service.ForgotPasswordService;
import com.nonmus.nonmus.modules.common.util.JwtUtil;
import com.nonmus.nonmus.modules.user.service.UsersService;
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

@WebMvcTest(ForgotPasswordController.class)
@AutoConfigureMockMvc(addFilters = false)
class ForgotPasswordControllerTest {

    @MockitoBean
    private AuthenticationService authService;

    @MockitoBean
    private ForgotPasswordService forgotPasswordService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UsersService usersService;

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
    void forgotPassword_shouldReturnOk() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("test@test.com");

        client.post()
                .uri("/api/v1/forgot-password/send")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .json("""
                        {
                          "message":"Verification code sent to your email"
                        }
                        """);

        verify(authService).forgotPassword(request);
    }

    @Test
    void forgotPasswordVerify_shouldReturnOk() {
        ForgotPasswordVerifyRequest request = new ForgotPasswordVerifyRequest();
        request.setEmail("test@test.com");
        request.setCode("123456");

        when(forgotPasswordService.verifyCode("test@test.com", "123456"))
                .thenReturn("reset-token");

        client.post()
                .uri("/api/v1/forgot-password/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .json("""
                        {
                          "message":"Verification Successful"
                        }
                        """);

        verify(forgotPasswordService).verifyCode("test@test.com", "123456");
    }

    @Test
    void resetPassword_shouldReturnNoContent() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setNewPassword("newPassword123");

        client.post()
                .uri("/api/v1/forgot-password/reset")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .cookie("reset_token", "test-reset-token")
                .exchange()
                .expectStatus().isNoContent();

        verify(forgotPasswordService)
                .resetPassword("test-reset-token", "newPassword123");
    }
}