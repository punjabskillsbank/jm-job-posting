package com.jobmatrix.service;

import com.jobmatrix.entity.JobPosting;

import java.util.List;
import java.util.UUID;

public interface FreelancerSavedJobPostingService {

    void saveJobPosting(UUID freelancerId, Long jobPostingId);

    void removeSavedJobPosting(UUID freelancerId, Long jobPostingId);

    List<JobPosting> getSavedJobPostings(UUID freelancerId);
}

