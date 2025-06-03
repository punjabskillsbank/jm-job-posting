package com.jobmatrix.service;

import com.jobmatrix.dto.JobPostingDTO;
import com.jobmatrix.dto.JobPostingUpdateRequest;
import com.jobmatrix.entity.Category;
import com.jobmatrix.entity.JobPosting;
import com.jobmatrix.entity.JobPostingStatus;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface JobPostingService {
    JobPostingDTO createJobPosting(JobPostingDTO jobPostingDTO);
    List<JobPosting> getOpenJobPostings();
    JobPosting getJobPostingById(Long jobPostingId);
    List<JobPosting> getJobPostingsByClientId(UUID clientId);
    Map<String, List<String>> getCategories();
    JobPosting updateJobPosting(Long job_Posting_Id, JobPostingUpdateRequest jobPostingUpdateRequest);
    Map<JobPostingStatus, List<JobPostingDTO>> getJobPostingsByStatuses(UUID clientId, List<JobPostingStatus> statusList);
} 