package com.jobmatrix.test_utils.factory;

import com.jobmatrix.dto.JobPostingDTO;
import com.jobmatrix.dto.JobPostingUpdateRequest;
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
                .jobPostingStatus(STATUS)
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
                .category(createMockCategory(2L, "Test Category", "Test Speciality"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static JobPosting createJobPostingEntity(UUID clientId, Long jobPostingId, String title, String description){
        return JobPosting.builder()
                .jobPostingId(jobPostingId)
                .clientId(clientId)
                .title(title)
                .description(description)
                .budgetType(BUDGET_TYPE)
                .hourlyMinRate(HOURLY_MIN_RATE)
                .hourlyMaxRate(HOURLY_MAX_RATE)
                .fixedPrice(FIXED_PRICE)
                .projectDuration(PROJECT_DURATION)
                .experienceLevel(EXPERIENCE_LEVEL)
                .jobPostingStatus(STATUS)
                .category(createMockCategory(5L, "Test Category", "Test Speciality"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static Category createMockCategory(Long categoryId, String category, String speciality) {
        return Category.builder()
                .categoryId(categoryId)
                .category(category)
                .speciality(speciality)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static JobPostingUpdateRequest createJobPostingUpdateRequest() {
        return JobPostingUpdateRequest.builder()
                .title("Updated " + TITLE)
                .description("Updated " + DESCRIPTION)
                .budgetType(BUDGET_TYPE)
                .hourlyMinRate(HOURLY_MIN_RATE + 10)
                .hourlyMaxRate(HOURLY_MAX_RATE + 10)
                .projectDuration(PROJECT_DURATION)
                .experienceLevel(EXPERIENCE_LEVEL)
                .categoryId(CATEGORY_ID)
                .build();
    }

    public static JobPosting createUpdatedJobPostingEntity(Long jobPostingId, UUID clientId, JobPostingUpdateRequest request, Category category, LocalDateTime createdAt) {
        return JobPosting.builder()
                .jobPostingId(jobPostingId)
                .clientId(clientId)
                .title(request.getTitle())
                .description(request.getDescription())
                .budgetType(request.getBudgetType())
                .hourlyMinRate(request.getHourlyMinRate())
                .hourlyMaxRate(request.getHourlyMaxRate())
                .fixedPrice(0)
                .projectDuration(request.getProjectDuration())
                .experienceLevel(request.getExperienceLevel())
                .jobPostingStatus(JobPostingStatus.OPEN)
                .category(category)
                .createdAt(createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
