package com.jobmatrix.serviceimpl;

import com.jobmatrix.entity.FreelancerSavedJob;
import com.jobmatrix.entity.JobPosting;
import com.jobmatrix.exceptionHandling.JobPostingNotFoundException;
import com.jobmatrix.repository.FreelancerSavedJobRepository;
import com.jobmatrix.repository.JobPostingRepository;
import com.jobmatrix.service.FreelancerSavedJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class FreelancerSavedJobServiceImpl implements FreelancerSavedJobService {

    private final JobPostingRepository jobPostingRepository;
    private final FreelancerSavedJobRepository savedJobRepository;

    @Override
    public void saveJob(UUID freelancerId, Long jobPostingId) {
        JobPosting jobPosting = jobPostingRepository.findById(jobPostingId)
                .orElseThrow(() -> new JobPostingNotFoundException(jobPostingId));

        boolean alreadySaved = savedJobRepository.existsByFreelancerIdAndJobPostingId(freelancerId, jobPostingId);
        if (!alreadySaved) {
            savedJobRepository.save(new FreelancerSavedJob(freelancerId, jobPostingId));
        }
    }

    @Override
    public void removeSavedJob(UUID freelancerId, Long jobPostingId) {
        savedJobRepository.deleteByFreelancerIdAndJobPostingId(freelancerId, jobPostingId);
    }

    @Override
    public List<JobPosting> getSavedJobs(UUID freelancerId) {
        List<FreelancerSavedJob> savedJobs = savedJobRepository.findAllByFreelancerId(freelancerId);
        List<Long> jobPostingIds = savedJobs.stream()
                .map(FreelancerSavedJob::getJobPostingId)
                .toList();

        return jobPostingRepository.findAllById(jobPostingIds);
    }
}
