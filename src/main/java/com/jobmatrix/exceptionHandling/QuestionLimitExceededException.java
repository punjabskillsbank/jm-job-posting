package com.jobmatrix.exceptionHandling;

public class QuestionLimitExceededException extends RuntimeException {
    public QuestionLimitExceededException() {
        super("Question Limit Exceeded. Maximum 5 questions are allowed.");
    }
}