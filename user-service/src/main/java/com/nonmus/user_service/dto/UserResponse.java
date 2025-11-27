package com.nonmus.user_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private boolean isEmailVerified;
    private String createdAt;
}
