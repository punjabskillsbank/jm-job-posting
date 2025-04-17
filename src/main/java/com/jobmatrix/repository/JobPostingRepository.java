package com.jobmatrix.repository;

import com.jobmatrix.entity.JobPosting;
import com.jobmatrix.entity.JobPostingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {
    List<JobPosting> findByJobPostingStatus(JobPostingStatus jobPostingStatus);

    List<JobPosting> findByClientId(UUID clientId);
} 