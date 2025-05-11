package com.jobmatrix.service;

import com.jobmatrix.dto.JobPostingDTO;
import com.jobmatrix.dto.JobPostingUpdateRequest;
import com.jobmatrix.entity.Category;
import com.jobmatrix.entity.JobPosting;

import java.util.List;
import java.util.UUID;

public interface JobPostingService {
    JobPosting createJobPosting(JobPostingDTO jobPostingDTO);
    List<JobPosting> getOpenJobPostings();
    JobPosting getJobPostingById(Long jobPostingId);
    List<JobPosting> getJobPostingsByClientId(UUID clientId);
    List<Category> getCategories();
    JobPosting updateJobPosting(Long job_Posting_Id, JobPostingUpdateRequest jobPostingUpdateRequest);
} 