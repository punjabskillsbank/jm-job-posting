package com.jobmatrix.serviceimpl;

import com.jobmatrix.dto.JobPostingDTO;
import com.jobmatrix.entity.Category;
import com.jobmatrix.entity.JobPosting;
import com.jobmatrix.entity.JobPostingStatus;
import com.jobmatrix.exceptionHandling.CategoryNotFoundException;
import com.jobmatrix.exceptionHandling.JobPostingNotFoundException;
import com.jobmatrix.repository.CategoryRepository;
import com.jobmatrix.repository.JobPostingRepository;
import com.jobmatrix.service.JobPostingService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobPostingServiceImpl implements JobPostingService {

    private final JobPostingRepository jobPostingRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public JobPosting createJobPosting(JobPostingDTO jobPostingDTO) {
        // Map DTO to Entity
        JobPosting jobPosting = modelMapper.map(jobPostingDTO, JobPosting.class);
        // Ensure it's treated as a new entity
        jobPosting.setJobPostingId(null);
        // Set default job posting status
        jobPosting.setJobPostingStatus(JobPostingStatus.DRAFT);
        // Fetch category and set it
        Category category = categoryRepository.findById(jobPostingDTO.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(jobPostingDTO.getCategoryId()));
        jobPosting.setCategory(category);
        // Save and return
        return jobPostingRepository.save(jobPosting);
    }

    @Override
    public List<JobPosting> getOpenJobPostings() {
        return jobPostingRepository.findByJobPostingStatus(JobPostingStatus.OPEN);

    }

    @Override
    public JobPosting getJobPostingById(Long jobPostingId) {

        return jobPostingRepository.findById(jobPostingId)
                .orElseThrow(() -> new JobPostingNotFoundException(jobPostingId));
    }

}
