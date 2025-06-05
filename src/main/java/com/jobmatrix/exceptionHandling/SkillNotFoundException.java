package com.jobmatrix.exceptionHandling;

public class SkillNotFoundException extends RuntimeException {

    public SkillNotFoundException(String message) {
        super(message);
    }
}
