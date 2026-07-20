package com.nonmus.nonmus.modules.auth.events;

import com.nonmus.nonmus.modules.user.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailForgotPasswordEvent {
    private Users user;
}
