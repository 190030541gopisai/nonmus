package com.nonmus.decoder;

import com.nonmus.constants.AppConstants;
import com.nonmus.exception.UserServiceException;

import feign.Response;
import feign.codec.ErrorDecoder;

public class UserServiceFeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        int status = response.status();

        if (status == 400) {
            return new UserServiceException(AppConstants.USER_BAD_REQUEST, "Bad request to user-service", status);
        }

        if (status == 404) {
            return new UserServiceException(AppConstants.USER_NOT_FOUND, "User endpoint not found", status);
        }

        if (status == 409) {
            return new UserServiceException(AppConstants.USER_CONFLICT, "User already exists", status);
        }

        return new UserServiceException(AppConstants.USER_SERVICE_UNAVAILABLE, "User-service is unavailable", 503);
    }
}
