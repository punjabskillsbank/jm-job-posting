package com.jobmatrix.exceptionHandling;

public class JobPostingNotFoundException extends RuntimeException {
  public JobPostingNotFoundException(Long jobPostingId) {
    super("JobPosting not found at given jobPostingId: " + jobPostingId);
  }

    public JobPostingNotFoundException(String message) {
        super(message);
    }
}
