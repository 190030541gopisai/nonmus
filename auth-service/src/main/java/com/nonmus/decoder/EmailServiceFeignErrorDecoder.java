package com.nonmus.decoder;

import com.nonmus.constants.AppConstants;
import com.nonmus.exception.EmailServiceException;

import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;

import java.io.IOException;

public class EmailServiceFeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        String responseBody = "";
        int status = response.status();

        try {
            if (response.body() != null) {
                responseBody = Util.toString(response.body().asReader());
            }
        } catch (IOException e) {
            responseBody = "Unable to read email-service error response";
        }

        return new EmailServiceException(status, responseBody);
    }
}