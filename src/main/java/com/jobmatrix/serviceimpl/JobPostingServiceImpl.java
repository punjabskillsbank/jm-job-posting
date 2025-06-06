package com.jobmatrix.serviceimpl;

import com.common.dto.CategoryDTO;
import com.common.exceptionHandling.ClientNotFoundException;
import com.common.dto.SkillDTO;
import com.jobmatrix.dto.JobPostingDTO;
import com.jobmatrix.dto.JobPostingUpdateRequest;
import com.jobmatrix.entity.Category;
import com.jobmatrix.entity.JobPosting;
import com.jobmatrix.entity.JobPostingStatus;
import com.jobmatrix.entity.Skill;
import com.jobmatrix.exceptionHandling.CategoryNotFoundException;
import com.jobmatrix.exceptionHandling.JobPostingNotFoundException;
import com.jobmatrix.exceptionHandling.SkillNotFoundException;
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
        // 1. Extract skill IDs from the DTO
        List<Long> skillIds = jobPostingDTO.getSkills() == null
                ? Collections.emptyList()
                : jobPostingDTO.getSkills().stream()
                .map(SkillDTO::getSkillId)
                .collect(Collectors.toList());

        // 2. Fetch Skill entities by IDs
        List<Skill> skills = skillRepository.findAllById(skillIds);

        // 3. Validate all requested skill IDs were found
        Set<Long> foundSkillIds = skills.stream()
                .map(Skill::getSkillId)
                .collect(Collectors.toSet());

        for (Long skillId : skillIds) {
            if (!foundSkillIds.contains(skillId)) {
                throw new SkillNotFoundException(skillId);
            }
        }

        // 4. Fetch and validate Category
        Long categoryId = jobPostingDTO.getCategory().getCategoryId();

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        // 5. Map DTO to Entity
        JobPosting jobPosting = modelMapper.map(jobPostingDTO, JobPosting.class);

        // 6. Set skills and category to job posting
        jobPosting.setSkills(new HashSet<>(skills));
        jobPosting.setCategory(category);

        // 7. Set default status if null
        if (jobPosting.getJobPostingStatus() == null) {
            jobPosting.setJobPostingStatus(JobPostingStatus.IN_REVIEW);
        }

        // 8. Save job posting
        JobPosting savedJobPosting = jobPostingRepository.save(jobPosting);

        // 9. Map saved entity back to DTO and return
        return modelMapper.map(savedJobPosting, JobPostingDTO.class);
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
