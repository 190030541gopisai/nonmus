package com.nonmus.decoder;

import org.springframework.stereotype.Component;

import com.nonmus.constants.AppConstants;
import com.nonmus.exception.UserServiceException;

import feign.Response;
import feign.codec.ErrorDecoder;

@Component
public class UserServiceFeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        int status = response.status();

        if(status == 400) {
            return new UserServiceException(
                AppConstants.USER_BAD_REQUEST,
                "Bad request to user service");
        }

        if(status == 404) {
            return new UserServiceException(
                AppConstants.USER_NOT_FOUND,
                "User not found");
        }

        if(status == 409) {
            return new UserServiceException(
                AppConstants.USER_CONFLICT,
                "User conflict");
        }

        return new UserServiceException(
            AppConstants.USER_SERVICE_UNAVAILABLE,
            "User service failed");
    }
}
