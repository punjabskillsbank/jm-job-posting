package com.jobmatrix.serviceimpl;

import com.common.exceptionHandling.ClientNotFoundException;
import com.jobmatrix.dto.JobPostingDTO;
import com.jobmatrix.dto.JobPostingUpdateRequest;
import com.jobmatrix.dto.SkillDTO;
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
        Category category = categoryRepository.findById(jobPostingDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        Set<Skill> skills = new HashSet<>();
        if (jobPostingDTO.getSkillIds() != null && !jobPostingDTO.getSkillIds().isEmpty()) {
            List<Skill> foundSkills = skillRepository.findAllById(jobPostingDTO.getSkillIds());
            if (foundSkills.size() != jobPostingDTO.getSkillIds().size()) {
                throw new RuntimeException("Some skillIds not found");
            }
            skills.addAll(foundSkills);
        }
        JobPosting jobPosting = modelMapper.map(jobPostingDTO, JobPosting.class);
        jobPosting.setCategory(category);
        jobPosting.setSkills(skills);
        if (jobPosting.getJobPostingStatus() == null) {
            jobPosting.setJobPostingStatus(JobPostingStatus.IN_REVIEW);
        }
        JobPosting savedJobPosting = jobPostingRepository.save(jobPosting);

        JobPostingDTO resultDTO = modelMapper.map(savedJobPosting, JobPostingDTO.class);
        if (savedJobPosting.getSkills() != null) {
            Set<SkillDTO> skillDTOs = savedJobPosting.getSkills().stream().map(skill -> {
                SkillDTO skillDTO = new SkillDTO();
                skillDTO.setSkillId(skill.getSkillId());
                skillDTO.setSkill(skill.getSkill());
                return skillDTO;
            }).collect(Collectors.toSet());
            resultDTO.setSkills(skillDTOs);
        }
        resultDTO.setSkillIds(null);
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
