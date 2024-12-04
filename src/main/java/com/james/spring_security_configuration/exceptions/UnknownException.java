package com.james.spring_security_configuration.exceptions;

public class UnknownException extends RuntimeException {
    public UnknownException(String message) {
        super(message);
    }
}
