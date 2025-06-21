package com.jobmatrix.service;

import com.jobmatrix.entity.JobPosting;

import java.util.List;
import java.util.UUID;

public interface FreelancerSavedJobService {

    void saveJob(UUID freelancerId, Long jobPostingId);

    void removeSavedJob(UUID freelancerId, Long jobPostingId);

    List<JobPosting> getSavedJobs(UUID freelancerId);
}

