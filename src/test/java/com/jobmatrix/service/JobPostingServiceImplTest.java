package com.jobmatrix.service;

import com.jobmatrix.dto.JobPostingDTO;
import com.jobmatrix.entity.Category;
import com.jobmatrix.entity.Client;
import com.jobmatrix.entity.JobPosting;
import com.jobmatrix.entity.JobPostingStatus;
import com.jobmatrix.exceptionHandling.JobPostingNotFoundException;
import com.jobmatrix.repository.CategoryRepository;
import com.jobmatrix.repository.ClientRepository;
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
import java.util.List;
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
    private ClientRepository clientRepository;

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


    @Test
    public void testGetOpenJobPostings_WhenSomeAreOpen() {
        JobPosting openJob1 = JobPostingTestDataFactory.createJobPostingEntity(UUID.randomUUID());
        JobPosting openJob2 = JobPostingTestDataFactory.createJobPostingEntity(UUID.randomUUID());

        openJob1.setJobPostingStatus(JobPostingStatus.OPEN);
        openJob2.setJobPostingStatus(JobPostingStatus.OPEN);

        when(jobPostingRepository.findByJobPostingStatus(JobPostingStatus.OPEN))
                .thenReturn(List.of(openJob1, openJob2));

        List<JobPosting> result = jobPostingService.getOpenJobPostings();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(job -> job.getJobPostingStatus() == JobPostingStatus.OPEN));

        verify(jobPostingRepository).findByJobPostingStatus(JobPostingStatus.OPEN);
    }

    @Test
    public void testGetOpenJobPostings_WhenNoneAreOpen() {
        when(jobPostingRepository.findByJobPostingStatus(JobPostingStatus.OPEN))
                .thenReturn(List.of());

        List<JobPosting> result = jobPostingService.getOpenJobPostings();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(jobPostingRepository).findByJobPostingStatus(JobPostingStatus.OPEN);
    }

    @Test
    public void testGetOpenJobPostings_WithMultipleStatuses_ShouldReturnOnlyOpenJobs() {
        JobPosting openJob1 = JobPostingTestDataFactory.createJobPostingEntity(UUID.randomUUID());
        JobPosting openJob2 = JobPostingTestDataFactory.createJobPostingEntity(UUID.randomUUID());
        JobPosting draftJob = JobPostingTestDataFactory.createJobPostingEntity(UUID.randomUUID());
        JobPosting inReviewJob = JobPostingTestDataFactory.createJobPostingEntity(UUID.randomUUID());
        JobPosting inProgressJob = JobPostingTestDataFactory.createJobPostingEntity(UUID.randomUUID());
        JobPosting closedJob = JobPostingTestDataFactory.createJobPostingEntity(UUID.randomUUID());
        JobPosting completedJob = JobPostingTestDataFactory.createJobPostingEntity(UUID.randomUUID());

        openJob1.setJobPostingStatus(JobPostingStatus.OPEN);
        openJob2.setJobPostingStatus(JobPostingStatus.OPEN);
        draftJob.setJobPostingStatus(JobPostingStatus.DRAFT);
        inReviewJob.setJobPostingStatus(JobPostingStatus.IN_REVIEW);
        inProgressJob.setJobPostingStatus(JobPostingStatus.IN_PROGRESS);
        closedJob.setJobPostingStatus(JobPostingStatus.CLOSED);
        completedJob.setJobPostingStatus(JobPostingStatus.COMPLETED);

        when(jobPostingRepository.findByJobPostingStatus(JobPostingStatus.OPEN))
                .thenReturn(List.of(openJob1, openJob2));

        List<JobPosting> result = jobPostingService.getOpenJobPostings();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(job -> job.getJobPostingStatus() == JobPostingStatus.OPEN));

        verify(jobPostingRepository, times(1)).findByJobPostingStatus(JobPostingStatus.OPEN);
    }

    @Test
    void getJobPostingById_shouldReturnJobPostingEntity() {
        long jobPostingId = 1L;
        JobPosting mockJobPosting = JobPostingTestDataFactory.createJobPostingEntity(UUID.randomUUID());
        mockJobPosting.setJobPostingId(jobPostingId);

        when(jobPostingRepository.findById(jobPostingId)).thenReturn(Optional.of(mockJobPosting));

        JobPosting result = jobPostingService.getJobPostingById(jobPostingId);

        assertNotNull(result);
        assertEquals(mockJobPosting.getJobPostingId(), result.getJobPostingId());
        assertEquals(mockJobPosting.getTitle(), result.getTitle());
        assertEquals(mockJobPosting.getDescription(), result.getDescription());
        assertEquals(mockJobPosting.getBudgetType(), result.getBudgetType());
        assertEquals(mockJobPosting.getHourlyMinRate(), result.getHourlyMinRate());
        assertEquals(mockJobPosting.getHourlyMaxRate(), result.getHourlyMaxRate());
        assertEquals(mockJobPosting.getProjectDuration(), result.getProjectDuration());
        assertEquals(mockJobPosting.getExperienceLevel(), result.getExperienceLevel());

        verify(jobPostingRepository, times(1)).findById(jobPostingId);
    }

    @Test
    void getJobPostingById_shouldThrowJobPostingNotFoundException() {
        long jobPostingId = 2L;

        when(jobPostingRepository.findById(jobPostingId)).thenReturn(Optional.empty());

        JobPostingNotFoundException exception = assertThrows(
                JobPostingNotFoundException.class,
                () -> jobPostingService.getJobPostingById(jobPostingId),
                "JobPosting not found at given jobPostingId"
        );

        assertEquals("JobPosting not found at given jobPostingId: " + jobPostingId, exception.getMessage());
        verify(jobPostingRepository, times(1)).findById(jobPostingId);
    }

    @Test
    void getJobPostingsByClientId_shouldReturnJobPostingsForExistingClient() {
        UUID clientId = UUID.randomUUID();
        JobPosting mockJobPosting1 = JobPostingTestDataFactory.createJobPostingEntity(clientId);
        JobPosting mockJobPosting2 = JobPostingTestDataFactory.createJobPostingEntity(clientId);

        // Mock client existence
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(new Client()));
        // Mock job postings
        when(jobPostingRepository.findByClientId(clientId)).thenReturn(List.of(mockJobPosting1, mockJobPosting2));

        List<JobPosting> result = jobPostingService.getJobPostingsByClientId(clientId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(mockJobPosting1.getClientId(), result.get(0).getClientId());
        assertEquals(mockJobPosting2.getClientId(), result.get(1).getClientId());

        verify(clientRepository, times(1)).findById(clientId);
        verify(jobPostingRepository, times(1)).findByClientId(clientId);
    }

    @Test
    void getJobPostingsByClientId_shouldThrowExceptionForNonExistingClient() {
        UUID clientId = UUID.randomUUID();

        // Mock client existence
        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        JobPostingNotFoundException exception = assertThrows(
                JobPostingNotFoundException.class,
                () -> jobPostingService.getJobPostingsByClientId(clientId),
                "Client not found"
        );

        assertEquals("Client not found with ID: " + clientId, exception.getMessage());
        verify(clientRepository, times(1)).findById(clientId);
        verify(jobPostingRepository, never()).findByClientId(clientId);
    }

    @Test
    void getJobPostingsByClientId_shouldThrowExceptionForClientWithNoPostings() {
        UUID clientId = UUID.randomUUID();

        // Mock client existence
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(new Client()));
        // Mock empty job postings
        when(jobPostingRepository.findByClientId(clientId)).thenReturn(List.of());

        JobPostingNotFoundException exception = assertThrows(
                JobPostingNotFoundException.class,
                () -> jobPostingService.getJobPostingsByClientId(clientId),
                "No job postings found"
        );

        assertEquals("No job postings found for client ID: " + clientId, exception.getMessage());
        verify(clientRepository, times(1)).findById(clientId);
        verify(jobPostingRepository, times(1)).findByClientId(clientId);
    }

    




}
