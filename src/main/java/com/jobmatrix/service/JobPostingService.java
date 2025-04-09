package com.jobmatrix.service;

import com.jobmatrix.dto.JobPostingDTO;
import com.jobmatrix.entity.JobPosting;

public interface JobPostingService {
    JobPosting createJobPosting(JobPostingDTO jobPostingDTO);
} 