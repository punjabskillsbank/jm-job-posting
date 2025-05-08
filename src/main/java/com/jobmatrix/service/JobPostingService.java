package com.jobmatrix.service;

import com.jobmatrix.dto.JobPostingDTO;
import com.jobmatrix.entity.Category;
import com.jobmatrix.entity.JobPosting;

import java.util.List;

public interface JobPostingService {
    JobPosting createJobPosting(JobPostingDTO jobPostingDTO);
    List<JobPosting> getOpenJobPostings();
    JobPosting getJobPostingById(Long jobPostingId);
    List<Category> getCategories();
} 