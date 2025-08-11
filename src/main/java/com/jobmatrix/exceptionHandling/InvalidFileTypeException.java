package com.jobmatrix.exceptionHandling;

public class InvalidFileTypeException extends RuntimeException {
    public InvalidFileTypeException(String extension) {
        super("File type not allowed: " + extension);
    }
}
