package com.jobmatrix.repository;

import com.common.entity.JobPosting;
import com.common.enums.JobPostingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {
    List<JobPosting> findByJobPostingStatus(JobPostingStatus jobPostingStatus);
    List<JobPosting> findByClientId(UUID clientId);
    List<JobPosting> findByClientIdAndJobPostingStatus(UUID clientId, JobPostingStatus status);
} 