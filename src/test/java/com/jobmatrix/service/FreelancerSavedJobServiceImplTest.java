package com.jobmatrix.service;

import com.jobmatrix.entity.FreelancerSavedJob;
import com.jobmatrix.entity.JobPosting;
import com.jobmatrix.exceptionHandling.JobPostingNotFoundException;
import com.jobmatrix.repository.FreelancerSavedJobRepository;
import com.jobmatrix.repository.JobPostingRepository;
import com.jobmatrix.serviceimpl.FreelancerSavedJobServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class FreelancerSavedJobServiceImplTest {

    @InjectMocks
    private FreelancerSavedJobServiceImpl service;

    @Mock
    private JobPostingRepository jobPostingRepository;

    @Mock
    private FreelancerSavedJobRepository savedJobRepository;

    private UUID freelancerId;
    private Long jobPostingId;
    private JobPosting jobPosting;

    @BeforeEach
    void setup() {
        freelancerId = UUID.randomUUID();
        jobPostingId = 123L;
        jobPosting = new JobPosting();
        jobPosting.setJobPostingId(jobPostingId); // or your setter
    }

    @Test
    void testSaveJob_successfullySavesIfNotAlreadyExists() {
        Mockito.when(jobPostingRepository.findById(jobPostingId))
                .thenReturn(Optional.of(jobPosting));
        Mockito.when(savedJobRepository.existsByFreelancerIdAndJobPostingId(freelancerId, jobPostingId))
                .thenReturn(false);

        service.saveJob(freelancerId, jobPostingId);

        Mockito.verify(savedJobRepository).save(Mockito.any(FreelancerSavedJob.class));
    }

    @Test
    void testSaveJob_alreadySaved_doesNotSaveAgain() {
        Mockito.when(jobPostingRepository.findById(jobPostingId))
                .thenReturn(Optional.of(jobPosting));
        Mockito.when(savedJobRepository.existsByFreelancerIdAndJobPostingId(freelancerId, jobPostingId))
                .thenReturn(true);

        service.saveJob(freelancerId, jobPostingId);

        Mockito.verify(savedJobRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void testSaveJob_throwsIfJobNotFound() {
        Mockito.when(jobPostingRepository.findById(jobPostingId))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(JobPostingNotFoundException.class, () -> {
            service.saveJob(freelancerId, jobPostingId);
        });
    }

    @Test
    void testRemoveSavedJob_shouldCallDeleteByFreelancerIdAndJobPostingId() {
        service.removeSavedJob(freelancerId, jobPostingId);
        Mockito.verify(savedJobRepository, Mockito.times(1))
                .deleteByFreelancerIdAndJobPostingId(freelancerId, jobPostingId);
    }

    @Test
    void testGetSavedJobs_shouldReturnJobPostings() {
        FreelancerSavedJob saved1 = new FreelancerSavedJob(freelancerId, 1L);
        FreelancerSavedJob saved2 = new FreelancerSavedJob(freelancerId, 2L);

        JobPosting job1 = new JobPosting();
        job1.setJobPostingId(1L);

        JobPosting job2 = new JobPosting();
        job2.setJobPostingId(2L);

        Mockito.when(savedJobRepository.findAllByFreelancerId(freelancerId))
                .thenReturn(List.of(saved1, saved2));

        Mockito.when(jobPostingRepository.findAllById(List.of(1L, 2L)))
                .thenReturn(List.of(job1, job2));

        List<JobPosting> result = service.getSavedJobs(freelancerId);

        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.contains(job1));
        Assertions.assertTrue(result.contains(job2));
    }

}
