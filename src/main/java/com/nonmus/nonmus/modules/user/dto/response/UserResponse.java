package com.nonmus.nonmus.modules.user.dto.response;

import lombok.Data;

@Data
public class UserResponse {
    private String name;
    private String email;
    private String profilePicture;
    private Boolean emailVerified;
}
