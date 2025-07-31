package com.jobmatrix.service;

import com.common.dto.JobPostingDTO;
import com.common.dto.SkillDTO;
import com.common.entity.JobPosting;
import com.common.enums.JobPostingStatus;
import com.jobmatrix.dto.JobPostingUpdateRequest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface JobPostingService {
    JobPostingDTO createJobPosting(JobPostingDTO jobPostingDTO);
    List<JobPostingDTO> getOpenJobPostings();
    JobPosting getJobPostingById(Long jobPostingId);
    List<JobPosting> getJobPostingsByClientId(UUID clientId);
    Map<String, List<String>> getCategories();
    JobPosting updateJobPosting(Long job_Posting_Id, JobPostingUpdateRequest jobPostingUpdateRequest);
    Map<JobPostingStatus, List<JobPostingDTO>> getJobPostingsByStatuses(UUID clientId, List<JobPostingStatus> statusList);
    List<SkillDTO> getAllSkills();
}