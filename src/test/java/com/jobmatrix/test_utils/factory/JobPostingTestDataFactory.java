package com.jobmatrix.test_utils.factory;

import com.jobmatrix.dto.JobPostingDTO;
import com.jobmatrix.entity.*;

import java.time.LocalDateTime;
import java.util.UUID;

public class JobPostingTestDataFactory {
    private static final String TITLE = "Sample Job";
    private static final String DESCRIPTION = "Sample job description";
    private static final BudgetType BUDGET_TYPE = BudgetType.HOURLY;
    private static final int HOURLY_MIN_RATE = 30;
    private static final int HOURLY_MAX_RATE = 60;
    private static final int FIXED_PRICE = 0; // Optional, set default if used
    private static final ProjectDuration PROJECT_DURATION = ProjectDuration.SHORT_TERM;
    private static final ExperienceLevel EXPERIENCE_LEVEL = ExperienceLevel.BEGINNER;
    private static final JobPostingStatus STATUS = JobPostingStatus.OPEN; // or any default status
    private static final long CATEGORY_ID = 1L;

    public static JobPostingDTO createJobPostingDTO(UUID clientId) {
        return JobPostingDTO.builder()
                .clientId(clientId)
                .title(TITLE)
                .description(DESCRIPTION)
                .budgetType(BUDGET_TYPE)
                .hourlyMinRate(HOURLY_MIN_RATE)
                .hourlyMaxRate(HOURLY_MAX_RATE)
                .projectDuration(PROJECT_DURATION)
                .experienceLevel(EXPERIENCE_LEVEL)
                .categoryId(CATEGORY_ID)
                .build();
    }

    public static JobPosting createJobPostingEntity(UUID clientId) {
        return JobPosting.builder()
                .jobPostingId(1L)
                .clientId(clientId)
                .title(TITLE)
                .description(DESCRIPTION)
                .budgetType(BUDGET_TYPE)
                .hourlyMinRate(HOURLY_MIN_RATE)
                .hourlyMaxRate(HOURLY_MAX_RATE)
                .fixedPrice(FIXED_PRICE)
                .projectDuration(PROJECT_DURATION)
                .experienceLevel(EXPERIENCE_LEVEL)
                .jobPostingStatus(STATUS)
                .category(createMockCategory())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    private static Category createMockCategory() {
        return Category.builder()
                .categoryId(CATEGORY_ID)
                .category("Sample Category")
                .speciality("Sample Speciality")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
