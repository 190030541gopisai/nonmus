package com.nonmus.decoder;

import java.io.IOException;

import com.nonmus.exception.DownStreamException;

import feign.Response;
import feign.Util;

public class FeignErrorUtils {
    static DownStreamException defaultError(Response response, String serviceName) {
        String responseBody = "";
        int status = response.status();

        try {
            if (response.body() != null) {
                responseBody = Util.toString(response.body().asReader());
            }
        } catch (IOException e) {
            responseBody = "Unable to read " + serviceName + " error response";
        }

        return new DownStreamException(status, responseBody);
    }
}
