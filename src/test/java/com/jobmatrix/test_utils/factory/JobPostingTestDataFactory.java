package com.jobmatrix.test_utils.factory;

import com.jobmatrix.dto.JobPostingDTO;
import com.jobmatrix.entity.BudgetType;
import com.jobmatrix.entity.ExperienceLevel;
import com.jobmatrix.entity.JobPosting;
import com.jobmatrix.entity.ProjectDuration;

import java.time.LocalDateTime;
import java.util.UUID;

public class JobPostingTestDataFactory {

    public static JobPostingDTO createDTO(UUID clientId) {
        JobPostingDTO dto = new JobPostingDTO();
        dto.setClientId(clientId);
        dto.setTitle("Sample Job");
        dto.setDescription("Sample job description");
        dto.setBudgetType(BudgetType.HOURLY);
        dto.setHourlyMinRate(30);
        dto.setHourlyMaxRate(60);
        dto.setProjectDuration(ProjectDuration.SHORT_TERM);
        dto.setExperienceLevel(ExperienceLevel.BEGINNER);
        dto.setCategoryId(1L);
        return dto;
    }

    public static JobPosting createEntity() {
        JobPosting job = new JobPosting();
        job.setJobPostingId(1L);
        job.setCreatedAt(LocalDateTime.now());
        job.setUpdatedAt(LocalDateTime.now());
        return job;
    }
}
