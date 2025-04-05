package com.jobmatrix.service;

import com.jobmatrix.dto.JobPostingDTO;
import com.jobmatrix.entity.Category;
import com.jobmatrix.entity.JobPosting;
import com.jobmatrix.entity.JobPostingStatus;
import com.jobmatrix.exceptionHandling.CategoryNotFoundException;
import com.jobmatrix.repository.CategoryRepository;
import com.jobmatrix.repository.JobPostingRepository;
import com.jobmatrix.serviceimpl.JobPostingServiceImpl;
import com.jobmatrix.test_utils.factory.JobPostingTestDataFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JobPostingServiceImplTest {

    @Mock
    private JobPostingRepository jobPostingRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private JobPostingServiceImpl jobPostingService;

    private JobPostingDTO jobPostingDTO;
    private Category mockCategory;

    @BeforeEach
    public void setUp() {
        UUID clientId = UUID.randomUUID();
        jobPostingDTO = JobPostingTestDataFactory.createDTO(clientId);

        mockCategory = new Category();
        mockCategory.setCategoryId(jobPostingDTO.getCategoryId());
        mockCategory.setCategory("Test Category");
        mockCategory.setSpeciality("Test Speciality");
    }

    @Test
    public void testCreateJobPosting_Success() {
        // Given
        when(categoryRepository.findById(jobPostingDTO.getCategoryId())).thenReturn(Optional.of(mockCategory));
        when(jobPostingRepository.save(any(JobPosting.class))).thenAnswer(invocation -> {
            JobPosting jobPosting = invocation.getArgument(0);
            jobPosting.setJobPostingId(1L);
            jobPosting.setCreatedAt(LocalDateTime.now());
            jobPosting.setUpdatedAt(LocalDateTime.now());
            return jobPosting;
        });

        // When
        JobPosting result = jobPostingService.createJobPosting(jobPostingDTO);

        // Then
        assertNotNull(result);
        assertEquals(jobPostingDTO.getTitle(), result.getTitle());
        assertEquals(jobPostingDTO.getDescription(), result.getDescription());
        assertEquals(jobPostingDTO.getBudgetType(), result.getBudgetType());
        assertEquals(jobPostingDTO.getHourlyMinRate(), result.getHourlyMinRate());
        assertEquals(jobPostingDTO.getHourlyMaxRate(), result.getHourlyMaxRate());
        assertEquals(jobPostingDTO.getProjectDuration(), result.getProjectDuration());
        assertEquals(jobPostingDTO.getExperienceLevel(), result.getExperienceLevel());
        assertEquals(JobPostingStatus.DRAFT, result.getJobPostingStatus());
        assertEquals(mockCategory, result.getCategory());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    public void testCreateJobPosting_CategoryNotFound() {
        // Given
        when(categoryRepository.findById(jobPostingDTO.getCategoryId())).thenReturn(Optional.empty());

        // When/Then
        CategoryNotFoundException exception = assertThrows(CategoryNotFoundException.class, () -> {
            jobPostingService.createJobPosting(jobPostingDTO);
        });

        assertEquals("Category not found", exception.getMessage());
    }
}
