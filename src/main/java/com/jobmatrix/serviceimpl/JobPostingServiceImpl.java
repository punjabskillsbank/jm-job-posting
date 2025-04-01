package com.jobmatrix.serviceimpl;

import com.jobmatrix.dto.JobPostingDTO;
import com.jobmatrix.entity.Category;
import com.jobmatrix.entity.JobPosting;
import com.jobmatrix.entity.JobPostingStatus;
import com.jobmatrix.repository.CategoryRepository;
import com.jobmatrix.repository.JobPostingRepository;
import com.jobmatrix.service.JobPostingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class JobPostingServiceImpl implements JobPostingService {

    @Autowired
    private JobPostingRepository jobPostingRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    @Transactional
    public JobPosting createJobPosting(JobPostingDTO jobPostingDTO) {
        JobPosting jobPosting = new JobPosting();
        jobPosting.setClientId(jobPostingDTO.getClientId());
        jobPosting.setTitle(jobPostingDTO.getTitle());
        jobPosting.setDescription(jobPostingDTO.getDescription());
        jobPosting.setBudgetType(jobPostingDTO.getBudgetType());
        jobPosting.setHourlyMinRate(jobPostingDTO.getHourlyMinRate());
        jobPosting.setHourlyMaxRate(jobPostingDTO.getHourlyMaxRate());
        jobPosting.setFixedPrice(jobPostingDTO.getFixedPrice());
        jobPosting.setProjectDuration(jobPostingDTO.getProjectDuration());
        jobPosting.setExperienceLevel(jobPostingDTO.getExperienceLevel());
        jobPosting.setJobPostingStatus(JobPostingStatus.DRAFT);
        
        Category category = categoryRepository.findById(jobPostingDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        jobPosting.setCategory(category);
        
        jobPosting.setCreatedAt(LocalDateTime.now());
        jobPosting.setUpdatedAt(LocalDateTime.now());
        
        return jobPostingRepository.save(jobPosting);
    }
} 