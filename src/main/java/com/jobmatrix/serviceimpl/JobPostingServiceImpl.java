package com.jobmatrix.serviceimpl;

import com.common.exceptionHandling.ClientNotFoundException;
import com.jobmatrix.dto.JobPostingDTO;
import com.jobmatrix.dto.JobPostingUpdateRequest;
import com.jobmatrix.entity.Category;
import com.jobmatrix.entity.JobPosting;
import com.jobmatrix.entity.JobPostingStatus;
import com.jobmatrix.entity.Skill;
import com.jobmatrix.exceptionHandling.CategoryNotFoundException;
import com.jobmatrix.exceptionHandling.JobPostingNotFoundException;
import com.jobmatrix.repository.CategoryRepository;
import com.jobmatrix.repository.ClientRepository;
import com.jobmatrix.repository.JobPostingRepository;
import com.jobmatrix.repository.SkillRepository;
import com.jobmatrix.service.JobPostingService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobPostingServiceImpl implements JobPostingService {

    private final JobPostingRepository jobPostingRepository;
    private final CategoryRepository categoryRepository;
    private final ClientRepository clientRepository;
    private final ModelMapper modelMapper;
    private final SkillRepository skillRepository;

    @Override
    @Transactional
    public JobPostingDTO createJobPosting(JobPostingDTO jobPostingDTO) {
        // Map DTO to Entity
        JobPosting jobPosting = modelMapper.map(jobPostingDTO, JobPosting.class);
        // Ensure it's treated as a new entity
        jobPosting.setJobPostingId(null);
        // Set default job posting status if null
        if (jobPostingDTO.getJobPostingStatus() == null) {
            jobPosting.setJobPostingStatus(JobPostingStatus.IN_REVIEW);
        }
        // Fetch and set category
        Category category = categoryRepository.findById(jobPostingDTO.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(jobPostingDTO.getCategoryId()));
        jobPosting.setCategory(category);

        // Fetch and set skills
        Set<Skill> skills = new HashSet<>();
        if (jobPostingDTO.getSkillIds() != null && !jobPostingDTO.getSkillIds().isEmpty()) {
            List<Skill> foundSkills = skillRepository.findAllById(jobPostingDTO.getSkillIds());
            if (foundSkills.size() != jobPostingDTO.getSkillIds().size()) {
                throw new RuntimeException("Some skillIds not found");
            }
            skills.addAll(foundSkills);
        }
        jobPosting.setSkills(skills);

        // Save entity
        JobPosting savedJobPosting = jobPostingRepository.save(jobPosting);

        // Map saved entity back to DTO
        JobPostingDTO resultDTO = modelMapper.map(savedJobPosting, JobPostingDTO.class);

        // Manually set IDs and other fields not mapped automatically
        setIdsInDto(resultDTO, savedJobPosting);

        return resultDTO;
    }


    private void setIdsInDto(JobPostingDTO dto, JobPosting entity) {
        dto.setJobPostingId(entity.getJobPostingId());
        dto.setCategoryId(entity.getCategory() != null ? entity.getCategory().getCategoryId() : null);

        if (entity.getSkills() != null && !entity.getSkills().isEmpty()) {
            Set<Long> skillIds = entity.getSkills().stream()
                    .map(Skill::getSkillId)
                    .collect(Collectors.toSet());
            dto.setSkillIds(skillIds);
        } else {
            dto.setSkillIds(Collections.emptySet());
        }

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
    public Map<String, List<String>> getCategories() {
        List<Category> categories = categoryRepository.findAll();

        return categories.stream()
                .collect(Collectors.groupingBy(
                        Category::getCategory,
                        Collectors.mapping(Category::getSpeciality, Collectors.toList())
                ));
    }

    @Transactional
    @Override
    public JobPosting updateJobPosting(Long job_posting_id, JobPostingUpdateRequest request) {

        JobPosting jobPosting = jobPostingRepository.findById(job_posting_id)
                .orElseThrow(() -> new JobPostingNotFoundException(job_posting_id));

        modelMapper.getConfiguration().setSkipNullEnabled(true);

        if (request.getCategoryId() == null) {
            modelMapper.map(request, jobPosting);
        } else {
            Long categoryId = request.getCategoryId();
            request.setCategoryId(null); // prevent automatic mapping of categoryId
            modelMapper.map(request, jobPosting);
            request.setCategoryId(categoryId); // restore it

            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new CategoryNotFoundException(categoryId));
            jobPosting.setCategory(category);
        }
        return jobPostingRepository.save(jobPosting);
    }

    @Override
    public Map<JobPostingStatus, List<JobPostingDTO>> getJobPostingsByStatuses(UUID clientId, List<JobPostingStatus> statusList) {
        if (clientRepository.findById(clientId).isEmpty()) {
            throw new ClientNotFoundException(clientId);
        }
        Map<JobPostingStatus, List<JobPostingDTO>> result = new HashMap<>();
        for (JobPostingStatus status : statusList) {
            List<JobPosting> jobPostings = jobPostingRepository.findByClientIdAndJobPostingStatus(clientId, status);
            List<JobPostingDTO> dtoList = new ArrayList<>();
            for (JobPosting jobPosting : jobPostings) {
                JobPostingDTO jobPostingDTO = modelMapper.map(jobPosting, JobPostingDTO.class);
                dtoList.add(jobPostingDTO);
            }
            result.put(status, dtoList);
        }
        return result;
    }
}
