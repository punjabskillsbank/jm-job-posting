package com.jobmatrix.integration;

import com.jobmatrix.BaseTest;
import com.jobmatrix.dto.JobPostingDTO;
import com.jobmatrix.entity.Category;
import com.jobmatrix.entity.JobPosting;
import com.jobmatrix.entity.JobPostingStatus;
import com.jobmatrix.entity.BudgetType;
import com.jobmatrix.entity.ProjectDuration;
import com.jobmatrix.entity.ExperienceLevel;
import com.jobmatrix.repository.CategoryRepository;
import com.jobmatrix.repository.JobPostingRepository;
import com.jobmatrix.service.JobPostingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class JobPostingIntegrationTest extends BaseTest {

    @Autowired
    private JobPostingService jobPostingService;

    @Autowired
    private JobPostingRepository jobPostingRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category category;
    private JobPostingDTO jobPostingDTO;

    @BeforeEach
    public void setUp() {
        super.setUp();

        // Create test category
        category = new Category();
        category.setCategory("Test Category");
        category.setSpeciality("Test Speciality");
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        category = categoryRepository.save(category);

        // Setup test DTO
        jobPostingDTO = new JobPostingDTO();
        jobPostingDTO.setClientId(UUID.randomUUID());
        jobPostingDTO.setTitle("Test Job");
        jobPostingDTO.setDescription("Test Description");
        jobPostingDTO.setBudgetType(BudgetType.HOURLY);
        jobPostingDTO.setHourlyMinRate(50);
        jobPostingDTO.setHourlyMaxRate(100);
        jobPostingDTO.setProjectDuration(ProjectDuration.SHORT_TERM);
        jobPostingDTO.setExperienceLevel(ExperienceLevel.BEGINNER);
        jobPostingDTO.setCategoryId(category.getCategoryId());
    }

    @Test
    public void testCreateAndRetrieveJobPosting() {
        // When
        JobPosting createdJobPosting = jobPostingService.createJobPosting(jobPostingDTO);
        Optional<JobPosting> retrievedJobPosting = jobPostingRepository.findById(createdJobPosting.getJobPostingId());

        // Then
        assertTrue(retrievedJobPosting.isPresent());
        JobPosting found = retrievedJobPosting.get();
        assertEquals(jobPostingDTO.getTitle(), found.getTitle());
        assertEquals(jobPostingDTO.getDescription(), found.getDescription());
        assertEquals(jobPostingDTO.getBudgetType(), found.getBudgetType());
        assertEquals(jobPostingDTO.getHourlyMinRate(), found.getHourlyMinRate());
        assertEquals(jobPostingDTO.getHourlyMaxRate(), found.getHourlyMaxRate());
        assertEquals(jobPostingDTO.getProjectDuration(), found.getProjectDuration());
        assertEquals(jobPostingDTO.getExperienceLevel(), found.getExperienceLevel());
        assertEquals(JobPostingStatus.DRAFT, found.getJobPostingStatus());
        assertEquals(category.getCategoryId(), found.getCategory().getCategoryId());
        assertNotNull(found.getCreatedAt());
        assertNotNull(found.getUpdatedAt());
    }

    @Test
    public void testCreateJobPostingWithNonExistentCategory() {
        // Given
        jobPostingDTO.setCategoryId(999L);

        // When/Then
        assertThrows(RuntimeException.class, () -> {
            jobPostingService.createJobPosting(jobPostingDTO);
        });
    }
} 