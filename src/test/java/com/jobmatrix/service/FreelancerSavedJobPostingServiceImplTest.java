package com.jobmatrix.service;

import com.common.entity.JobPosting;
import com.common.exceptionHandling.JobPostingNotFoundException;
import com.jobmatrix.entity.FreelancerSavedJobPosting;
import com.jobmatrix.repository.FreelancerSavedJobRepository;
import com.jobmatrix.repository.JobPostingRepository;
import com.jobmatrix.serviceimpl.FreelancerSavedJobPostingServiceImpl;
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
class FreelancerSavedJobPostingServiceImplTest {

    @InjectMocks
    private FreelancerSavedJobPostingServiceImpl service;

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
    void testSaveJobPosting_successfullySavesIfNotAlreadyExists() {
        Mockito.when(jobPostingRepository.findById(jobPostingId))
                .thenReturn(Optional.of(jobPosting));
        Mockito.when(savedJobRepository.existsByFreelancerIdAndJobPostingId(freelancerId, jobPostingId))
                .thenReturn(false);

        service.saveJobPosting(freelancerId, jobPostingId);

        Mockito.verify(savedJobRepository).save(Mockito.any(FreelancerSavedJobPosting.class));
    }

    @Test
    void testSaveJobPosting_alreadySaved_doesNotSaveAgain() {
        Mockito.when(jobPostingRepository.findById(jobPostingId))
                .thenReturn(Optional.of(jobPosting));
        Mockito.when(savedJobRepository.existsByFreelancerIdAndJobPostingId(freelancerId, jobPostingId))
                .thenReturn(true);

        service.saveJobPosting(freelancerId, jobPostingId);

        Mockito.verify(savedJobRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void testSaveJobPosting_throwsIfJobNotFound() {
        Mockito.when(jobPostingRepository.findById(jobPostingId))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(JobPostingNotFoundException.class, () -> {
            service.saveJobPosting(freelancerId, jobPostingId);
        });
    }

    @Test
    void testRemoveSavedJobPosting_shouldCallDeleteByFreelancerIdAndJobPostingId() {
        service.removeSavedJobPosting(freelancerId, jobPostingId);
        Mockito.verify(savedJobRepository, Mockito.times(1))
                .deleteByFreelancerIdAndJobPostingId(freelancerId, jobPostingId);
    }

    @Test
    void testGetSavedJobPostings_shouldReturnJobPostings() {
        FreelancerSavedJobPosting saved1 = new FreelancerSavedJobPosting(null, freelancerId, 123L);
        FreelancerSavedJobPosting saved2 = new FreelancerSavedJobPosting(null, freelancerId, 456L);

        JobPosting job1 = new JobPosting();
        job1.setJobPostingId(123L);

        JobPosting job2 = new JobPosting();
        job2.setJobPostingId(456L);

        Mockito.when(savedJobRepository.findAllByFreelancerId(freelancerId))
                .thenReturn(List.of(saved1, saved2));

        Mockito.when(jobPostingRepository.findAllById(List.of(123L, 456L)))
                .thenReturn(List.of(job1, job2));

        List<JobPosting> result = service.getSavedJobPostings(freelancerId);

        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.contains(job1));
        Assertions.assertTrue(result.contains(job2));
    }

}
