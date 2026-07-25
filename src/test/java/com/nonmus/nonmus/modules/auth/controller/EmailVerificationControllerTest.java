package com.nonmus.nonmus.modules.auth.controller;

import com.nonmus.nonmus.modules.auth.service.VerificationTokenService;
import com.nonmus.nonmus.modules.common.util.AuthUtil;
import com.nonmus.nonmus.modules.common.util.JwtUtil;
import com.nonmus.nonmus.modules.user.service.UsersService;
import com.nonmus.nonmus.security.AuthenticatedUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.Collections;

import static org.mockito.Mockito.*;

@WebMvcTest(value = EmailVerificationController.class)
@AutoConfigureMockMvc(addFilters = false)
class EmailVerificationControllerTest {

    @MockitoBean
    private VerificationTokenService verificationTokenService;

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
    void resendEmailVerificationLink_shouldReturnOk() {
        AuthenticatedUser user = new AuthenticatedUser();
        user.setEmail("john@test.com");

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        Collections.emptyList())
        );

        try (MockedStatic<AuthUtil> mocked = mockStatic(AuthUtil.class)) {
            mocked.when(AuthUtil::getPrincipal)
                    .thenReturn(user);

            client.post()
                    .uri("/api/v1/email/resend")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .json("""
                            {
                              "message":"Email verification sent successful"
                            }
                            """);

            verify(verificationTokenService)
                    .resend("john@test.com");
        }
    }

    @Test
    void verifyEmail_shouldReturnOk() {

        client.get()
                .uri("/api/v1/email/verify?token=test-token")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .json("""
                        {
                          "message":"Verification Successful"
                        }
                        """);

        verify(verificationTokenService)
                .verify("test-token");
    }
}