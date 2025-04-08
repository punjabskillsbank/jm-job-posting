package com.jobmatrix.service;

import com.jobmatrix.dto.JobPostingDTO;
import com.jobmatrix.entity.Category;
import com.jobmatrix.entity.JobPosting;
import com.jobmatrix.entity.JobPostingStatus;
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
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JobPostingServiceImplTest {

    @Mock
    private JobPostingRepository jobPostingRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private JobPostingServiceImpl jobPostingService;

    private JobPostingDTO jobPostingDTO;
    private Category category;

    @BeforeEach
    public void setUp() {
        UUID clientId = UUID.randomUUID();
        jobPostingDTO = JobPostingTestDataFactory.createJobPostingDTO(clientId);

        category = new Category();
        category.setCategoryId(jobPostingDTO.getCategoryId());
        category.setCategory("Test Category");
        category.setSpeciality("Test Speciality");
    }

    @Test
    public void testCreateJobPosting_Success() {
        // Given
        JobPosting jobPosting = new JobPosting();
        jobPosting.setTitle(jobPostingDTO.getTitle());
        jobPosting.setDescription(jobPostingDTO.getDescription());
        jobPosting.setBudgetType(jobPostingDTO.getBudgetType());
        jobPosting.setHourlyMinRate(jobPostingDTO.getHourlyMinRate());
        jobPosting.setHourlyMaxRate(jobPostingDTO.getHourlyMaxRate());
        jobPosting.setProjectDuration(jobPostingDTO.getProjectDuration());
        jobPosting.setExperienceLevel(jobPostingDTO.getExperienceLevel());

        when(categoryRepository.findById(jobPostingDTO.getCategoryId())).thenReturn(Optional.of(category));
        when(modelMapper.map(jobPostingDTO, JobPosting.class)).thenReturn(jobPosting);
        when(jobPostingRepository.save(any(JobPosting.class))).thenAnswer(invocation -> {
            JobPosting savedJob = invocation.getArgument(0);
            savedJob.setJobPostingId(1L);
            savedJob.setCreatedAt(LocalDateTime.now());
            savedJob.setUpdatedAt(LocalDateTime.now());
            return savedJob;
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
        assertEquals(category, result.getCategory());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        // Verify interactions
        verify(categoryRepository, times(1)).findById(jobPostingDTO.getCategoryId());
        verify(jobPostingRepository, times(1)).save(any(JobPosting.class));
        verify(modelMapper, times(1)).map(jobPostingDTO, JobPosting.class);
    }
}
