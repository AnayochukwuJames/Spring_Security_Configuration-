package com.james.spring_security_configuration.exceptions;

public class UnathorizedException extends RuntimeException {

    public UnathorizedException(String message) {
        super(message);
    }
}
