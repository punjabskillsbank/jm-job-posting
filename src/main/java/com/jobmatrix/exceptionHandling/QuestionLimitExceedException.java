package com.jobmatrix.exceptionHandling;

public class QuestionLimitExceedException extends RuntimeException {
    public QuestionLimitExceedException() {
        super("Question Limit Exceeded. Maximum 5 questions are allowed.");
    }
}