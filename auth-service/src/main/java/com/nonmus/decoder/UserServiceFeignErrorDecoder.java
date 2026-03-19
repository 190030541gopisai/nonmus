package com.nonmus.decoder;

import com.nonmus.constants.AppConstants;
import com.nonmus.exception.UserServiceException;

import feign.Response;
import feign.codec.ErrorDecoder;

public class UserServiceFeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        if(methodKey.contains("createUser")) {
            return handleCreateUserErrors(response);
        }
        return new UserServiceException(AppConstants.USER_SERVICE_UNAVAILABLE, "User-service is unavailable", response.status());
    }

    private Exception handleCreateUserErrors(Response response) {
        int status = response.status();

        if (status == 400) {
            return new UserServiceException(AppConstants.USER_BAD_REQUEST, "Bad request to user-service", status);
        }

        if (status == 404) {
            return new UserServiceException(AppConstants.USER_ENDPOINT_NOT_FOUND, "User endpoint not found", status);
        }

        if (status == 409) {
            return new UserServiceException(AppConstants.USER_CONFLICT, "User already exists", status);
        }

        return new UserServiceException(AppConstants.INTERNAL_SERVER_ERROR, "Internal server error in user-service", status);
    }
}
