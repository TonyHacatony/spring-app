package com.everamenkou.springapp.auth.errorhandling.exception;

public class AuthException extends ApiException {

    public AuthException(String message, String errorCode) {
        super(message, errorCode);
    }
}
