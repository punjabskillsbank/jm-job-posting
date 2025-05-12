package com.jobmatrix.serviceimpl;

import com.common.exceptionHandling.ClientNotFoundException;
import com.jobmatrix.dto.JobPostingDTO;
import com.jobmatrix.dto.JobPostingUpdateRequest;
import com.jobmatrix.entity.Category;
import com.jobmatrix.entity.JobPosting;
import com.jobmatrix.entity.JobPostingStatus;
import com.jobmatrix.exceptionHandling.CategoryNotFoundException;
import com.jobmatrix.exceptionHandling.JobPostingNotFoundException;
import com.jobmatrix.repository.CategoryRepository;
import com.jobmatrix.repository.ClientRepository;
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
    private final ClientRepository clientRepository;
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

    @Override
    public List<JobPosting> getJobPostingsByClientId(UUID clientId) {
        // Check if client exists
        if (clientRepository.findById(clientId).isEmpty()) {
            throw new ClientNotFoundException(clientId);
        }

        List<JobPosting> jobPostings = jobPostingRepository.findByClientId(clientId);

        return jobPostings;
    }

    @Override
    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    @Transactional
    @Override
    public JobPosting updateJobPosting(Long job_Posting_Id, JobPostingUpdateRequest request) {

        JobPosting jobPosting = jobPostingRepository.findById(job_Posting_Id)
                .orElseThrow(() -> new JobPostingNotFoundException(job_Posting_Id));

        modelMapper.getConfiguration().setSkipNullEnabled(true);

        // Temporarily exclude category from mapping
        Long categoryId = request.getCategoryId();
        request.setCategoryId(null);
        modelMapper.map(request, jobPosting);
        request.setCategoryId(categoryId);

        // Manually handle category if present
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new CategoryNotFoundException(categoryId));
            jobPosting.setCategory(category);
        }

        return jobPostingRepository.save(jobPosting);
    }

}
