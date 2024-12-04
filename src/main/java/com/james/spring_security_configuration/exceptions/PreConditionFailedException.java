package com.james.spring_security_configuration.exceptions;

public class PreConditionFailedException extends RuntimeException {

    public PreConditionFailedException(String message) {
        super(message);
    }
}
