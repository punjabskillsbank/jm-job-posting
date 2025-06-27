package com.jobmatrix.serviceimpl;

import com.jobmatrix.entity.FreelancerSavedJobPosting;
import com.jobmatrix.exceptionHandling.JobPostingNotFoundException;
import com.jobmatrix.repository.FreelancerSavedJobRepository;
import com.jobmatrix.repository.JobPostingRepository;
import com.jobmatrix.service.FreelancerSavedJobPostingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class FreelancerSavedJobPostingServiceImpl implements FreelancerSavedJobPostingService {

    private final JobPostingRepository jobPostingRepository;
    private final FreelancerSavedJobRepository savedJobRepository;

    @Override
    public void saveJobPosting(UUID freelancerId, Long jobPostingId) {
        JobPosting jobPosting = jobPostingRepository.findById(jobPostingId)
                .orElseThrow(() -> new JobPostingNotFoundException(jobPostingId));

        boolean alreadySaved = savedJobRepository.existsByFreelancerIdAndJobPostingId(freelancerId, jobPostingId);
        if (!alreadySaved) {
            savedJobRepository.save(new FreelancerSavedJobPosting(null,freelancerId, jobPosting.getJobPostingId()));
        }
    }

    @Override
    public void removeSavedJobPosting(UUID freelancerId, Long jobPostingId) {
        savedJobRepository.deleteByFreelancerIdAndJobPostingId(freelancerId, jobPostingId);
    }

    @Override
    public List<JobPosting> getSavedJobPostings(UUID freelancerId) {
        List<FreelancerSavedJobPosting> savedJobPostings = savedJobRepository.findAllByFreelancerId(freelancerId);
        List<Long> jobPostingIds = savedJobPostings.stream()
                .map(FreelancerSavedJobPosting::getJobPostingId)
                .toList();

        return jobPostingRepository.findAllById(jobPostingIds);
    }
}
