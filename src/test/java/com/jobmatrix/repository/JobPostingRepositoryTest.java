package com.jobmatrix.repository;

import com.jobmatrix.BaseTest;
import com.jobmatrix.entity.Category;
import com.jobmatrix.entity.JobPosting;
import com.jobmatrix.entity.JobPostingStatus;
import com.jobmatrix.entity.BudgetType;
import com.jobmatrix.entity.ProjectDuration;
import com.jobmatrix.entity.ExperienceLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class JobPostingRepositoryTest extends BaseTest {

    @Autowired
    private JobPostingRepository jobPostingRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category category;
    private JobPosting jobPosting;

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

        // Create test job posting
        jobPosting = new JobPosting();
        jobPosting.setClientId(UUID.randomUUID());
        jobPosting.setTitle("Test Job");
        jobPosting.setDescription("Test Description");
        jobPosting.setBudgetType(BudgetType.HOURLY);
        jobPosting.setHourlyMinRate(50);
        jobPosting.setHourlyMaxRate(100);
        jobPosting.setProjectDuration(ProjectDuration.SHORT_TERM);
        jobPosting.setExperienceLevel(ExperienceLevel.BEGINNER);
        jobPosting.setJobPostingStatus(JobPostingStatus.DRAFT);
        jobPosting.setCategory(category);
        jobPosting.setCreatedAt(LocalDateTime.now());
        jobPosting.setUpdatedAt(LocalDateTime.now());
        jobPosting = jobPostingRepository.save(jobPosting);
    }

    @Test
    public void testSaveAndFindById() {
        // When
        Optional<JobPosting> found = jobPostingRepository.findById(jobPosting.getJobPostingId());

        // Then
        assertTrue(found.isPresent());
        JobPosting foundJobPosting = found.get();
        assertEquals(jobPosting.getTitle(), foundJobPosting.getTitle());
        assertEquals(jobPosting.getDescription(), foundJobPosting.getDescription());
        assertEquals(jobPosting.getBudgetType(), foundJobPosting.getBudgetType());
        assertEquals(jobPosting.getHourlyMinRate(), foundJobPosting.getHourlyMinRate());
        assertEquals(jobPosting.getHourlyMaxRate(), foundJobPosting.getHourlyMaxRate());
        assertEquals(jobPosting.getProjectDuration(), foundJobPosting.getProjectDuration());
        assertEquals(jobPosting.getExperienceLevel(), foundJobPosting.getExperienceLevel());
        assertEquals(jobPosting.getJobPostingStatus(), foundJobPosting.getJobPostingStatus());
        assertEquals(jobPosting.getCategory().getCategoryId(), foundJobPosting.getCategory().getCategoryId());
    }

    @Test
    public void testFindByNonExistentId() {
        // When
        Optional<JobPosting> found = jobPostingRepository.findById(999L);

        // Then
        assertFalse(found.isPresent());
    }
} 