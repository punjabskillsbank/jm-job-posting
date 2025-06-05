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
        // 1. Extract and validate categoryId from CategoryDTO
        Long categoryId = (jobPostingDTO.getCategory() != null) ? jobPostingDTO.getCategory().getCategoryId() : null;
        if (categoryId == null) {
            throw new CategoryNotFoundException(-1L);
        }

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        // 2. Extract and validate skills from SkillDTOs
        Set<Skill> skills = new HashSet<>();
        if (jobPostingDTO.getSkills() != null && !jobPostingDTO.getSkills().isEmpty()) {
            Set<Long> skillIds = jobPostingDTO.getSkills().stream()
                    .map(SkillDTO::getSkillId)
                    .collect(Collectors.toSet());

            List<Skill> foundSkills = skillRepository.findAllById(skillIds);
            if (foundSkills.size() != skillIds.size()) {
                throw new SkillNotFoundException("Some skill IDs are invalid or not found");
            }

            skills.addAll(foundSkills);
        }

        // 3. Map DTO to entity
        JobPosting jobPosting = modelMapper.map(jobPostingDTO, JobPosting.class);
        jobPosting.setJobPostingId(null); // Ensure it's a new entity
        jobPosting.setCategory(category);
        jobPosting.setSkills(skills);

        // 4. Set default status if not provided
        if (jobPosting.getJobPostingStatus() == null) {
            jobPosting.setJobPostingStatus(JobPostingStatus.IN_REVIEW);
        }

        // 5. Save the entity
        JobPosting savedJobPosting = jobPostingRepository.save(jobPosting);

        // 6. Map saved entity to DTO
        JobPostingDTO resultDTO = modelMapper.map(savedJobPosting, JobPostingDTO.class);

        // 7. Manually populate CategoryDTO (including speciality)
        resultDTO.setCategory(CategoryDTO.builder()
                .categoryId(savedJobPosting.getCategory().getCategoryId())
                .category(savedJobPosting.getCategory().getCategory())
                .speciality(savedJobPosting.getCategory().getSpeciality())
                .build());

        // 8. Populate full SkillDTOs
        if (savedJobPosting.getSkills() != null) {
            Set<SkillDTO> skillDTOs = savedJobPosting.getSkills().stream()
                    .map(skill -> SkillDTO.builder()
                            .skillId(skill.getSkillId())
                            .skill(skill.getSkill())
                            .build())
                    .collect(Collectors.toSet());
            resultDTO.setSkills(skillDTOs);
        }

        return resultDTO;
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
