package com.nonmus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.nonmus.decoder.UserServiceFeignErrorDecoder;

import feign.codec.ErrorDecoder;

@Configuration
public class UserServiceFeignConfig {
    @Bean
    public ErrorDecoder errorDecoder() {
        return new UserServiceFeignErrorDecoder();
    }
}