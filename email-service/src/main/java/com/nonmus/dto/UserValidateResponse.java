package com.nonmus.dto;

import lombok.Data;

@Data
public class UserValidateResponse {
    private boolean userIdExist;
    private boolean emailExist;
    private boolean belongsToSameUser;
}
