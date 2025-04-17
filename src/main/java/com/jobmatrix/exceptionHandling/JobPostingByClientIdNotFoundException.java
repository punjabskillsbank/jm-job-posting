package com.jobmatrix.exceptionHandling;

import java.util.UUID;

public class JobPostingByClientIdNotFoundException extends RuntimeException {
    public JobPostingByClientIdNotFoundException(UUID clientId) {
        super("No job postings found for client ID: " + clientId);
    }
}
