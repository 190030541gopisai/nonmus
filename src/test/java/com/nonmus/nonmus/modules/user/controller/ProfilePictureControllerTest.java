package com.nonmus.nonmus.modules.user.controller;

import com.nonmus.nonmus.modules.common.util.AuthUtil;
import com.nonmus.nonmus.modules.common.util.JwtUtil;
import com.nonmus.nonmus.modules.user.dto.request.ConfirmUploadRequest;
import com.nonmus.nonmus.modules.user.dto.request.PresignedUrlRequest;
import com.nonmus.nonmus.modules.user.dto.response.PresignedUrlResponse;
import com.nonmus.nonmus.modules.user.dto.response.ViewUrlResponse;
import com.nonmus.nonmus.modules.user.service.ProfilePictureService;

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

import static org.mockito.Mockito.*;

@WebMvcTest(ProfilePictureController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProfilePictureControllerTest {

    @MockitoBean
    private ProfilePictureService profilePictureService;

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
    void getPresignedUploadUrl_shouldReturnPresignedUrl() {
        AuthenticatedUser user = new AuthenticatedUser();
        user.setEmail("user@test.com");

        PresignedUrlResponse serviceResponse = new PresignedUrlResponse(
                "https://s3.amazonaws.com/presigned-url", "users/user@test.com/key.jpg", 900);

        try (MockedStatic<AuthUtil> authUtil = mockStatic(AuthUtil.class)) {
            authUtil.when(AuthUtil::getPrincipal).thenReturn(user);

            when(profilePictureService.generatePresignedUploadUrl(
                    new PresignedUrlRequest("image/jpeg", 50000), "user@test.com"))
                    .thenReturn(serviceResponse);

            client.post()
                    .uri("/api/v1/users/profile-picture/presigned-url")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new PresignedUrlRequest("image/jpeg", 50000))
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.presignedUrl").isEqualTo("https://s3.amazonaws.com/presigned-url")
                    .jsonPath("$.s3Key").isEqualTo("users/user@test.com/key.jpg")
                    .jsonPath("$.expiresInSeconds").isEqualTo(900);
        }
    }

    @Test
    void confirmUpload_shouldReturnViewUrl() {
        AuthenticatedUser user = new AuthenticatedUser();
        user.setEmail("user@test.com");

        ViewUrlResponse serviceResponse = new ViewUrlResponse("https://s3.amazonaws.com/view-url", 900);

        try (MockedStatic<AuthUtil> authUtil = mockStatic(AuthUtil.class)) {
            authUtil.when(AuthUtil::getPrincipal).thenReturn(user);

            when(profilePictureService.confirmUpload(
                    new ConfirmUploadRequest("users/user@test.com/key.jpg"), "user@test.com"))
                    .thenReturn(serviceResponse);

            client.put()
                    .uri("/api/v1/users/profile-picture/confirm")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ConfirmUploadRequest("users/user@test.com/key.jpg"))
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.viewUrl").isEqualTo("https://s3.amazonaws.com/view-url")
                    .jsonPath("$.expiresInSeconds").isEqualTo(900);
        }
    }

    @Test
    void getViewUrl_shouldReturnViewUrl() {
        AuthenticatedUser user = new AuthenticatedUser();
        user.setEmail("user@test.com");

        ViewUrlResponse serviceResponse = new ViewUrlResponse("https://s3.amazonaws.com/view-url", 900);

        try (MockedStatic<AuthUtil> authUtil = mockStatic(AuthUtil.class)) {
            authUtil.when(AuthUtil::getPrincipal).thenReturn(user);

            when(profilePictureService.getViewUrl("user@test.com")).thenReturn(serviceResponse);

            client.get()
                    .uri("/api/v1/users/profile-picture/view-url")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.viewUrl").isEqualTo("https://s3.amazonaws.com/view-url")
                    .jsonPath("$.expiresInSeconds").isEqualTo(900);
        }
    }
}