package com.everamenkou.springapp.auth.errorhandling;

import com.everamenkou.springapp.auth.errorhandling.exception.ApiException;
import com.everamenkou.springapp.auth.errorhandling.exception.AuthException;
import com.everamenkou.springapp.auth.errorhandling.exception.UnauthorizedException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class AppErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(request, ErrorAttributeOptions.defaults());
        Throwable error = getError(request);

        ArrayList<Map<String, Object>> errorList = new ArrayList<>();
        HttpStatus status;

        if (error instanceof AuthException || error instanceof UnauthorizedException
                || error instanceof ExpiredJwtException || error instanceof MalformedJwtException) {
            status = HttpStatus.UNAUTHORIZED;
            String code = error instanceof ApiException apiEx ? apiEx.getErrorCode() : "JWT_HANDLING_ERROR";
            errorList.add(createErrorMap(code, error.getMessage()));
        } else if (error instanceof ApiException) {
            status = HttpStatus.BAD_REQUEST;
            String code = ((ApiException) error).getErrorCode();
            errorList.add(createErrorMap(code, error.getMessage()));
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            var message = error.getMessage();
            if (message == null)
                message = error.getClass().getName();

            errorList.add(createErrorMap("INTERNAL_ERROR", message));
        }

        HashMap<String, Object> errors = new HashMap<>();
        errors.put("errors", errorList);
        errorAttributes.put("status", status.value());
        errorAttributes.put("errors", errors);

        return errorAttributes;
    }

    private Map<String, Object> createErrorMap(String errorCode, String message) {
        LinkedHashMap<String, Object> errorMap = new LinkedHashMap<>();
        errorMap.put("code", errorCode);
        errorMap.put("message", message);
        return errorMap;
    }
}