package com.nonmus.nonmus.modules.user.dto.request;

import lombok.Data;

@Data
public class UserCreateRequest {
    private String name;
    private String email;
    private String password;
}
