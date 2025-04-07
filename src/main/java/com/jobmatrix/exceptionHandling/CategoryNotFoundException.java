package com.jobmatrix.exceptionHandling;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(Long categoryId) {
        super("Category not found at given categoryId: " + categoryId );
    }
}
