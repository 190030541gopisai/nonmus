package com.nonmus.service;

import org.springframework.stereotype.Service;

import com.nonmus.dto.TokenData;
import com.nonmus.dto.UserData;

@Service
public class TokenService {
    public TokenData generateTokenDataForUser(UserData userData) {
        return new TokenData();
    }
}
