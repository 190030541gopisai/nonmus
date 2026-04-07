package com.nonmus.decoder;

import com.nonmus.constants.AppConstants;
import feign.Response;
import feign.codec.ErrorDecoder;

public class EmailServiceFeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
       return FeignErrorUtils.defaultError(response, AppConstants.EMAIL_SERVICE);
    }
}