package com.jobmatrix.serviceimpl;

import com.common.entity.Category;
import com.common.entity.JobPosting;
import com.common.entity.JobPostingQuestion;
import com.common.entity.Skill;
import com.common.enums.JobPostingStatus;
import com.common.exceptionHandling.ClientNotFoundException;
import com.common.dto.SkillDTO;
import com.jobmatrix.dto.JobPostingDTO;
import com.jobmatrix.dto.JobPostingQuestionDTO;
import com.jobmatrix.dto.JobPostingUpdateRequest;
import com.jobmatrix.exceptionHandling.CategoryNotFoundException;
import com.jobmatrix.exceptionHandling.JobPostingNotFoundException;

import com.jobmatrix.exceptionHandling.QuestionLimitExceededException;

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

        List<Long> skillIds = Optional.ofNullable(jobPostingDTO.getSkills())
                .orElse(Collections.emptySet())
                .stream()
                .map(SkillDTO::getSkillId)
                .collect(Collectors.toList());

        List<Skill> skills = skillRepository.findAllById(skillIds);
        Set<Long> foundSkillIds = skills.stream()
                .map(Skill::getSkillId)
                .collect(Collectors.toSet());

        for (Long skillId : skillIds) {
            if (!foundSkillIds.contains(skillId)) {
                throw new SkillNotFoundException(skillId);
            }
        }
        Long categoryId = jobPostingDTO.getCategory().getCategoryId();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        jobPostingDTO.setJobPostingId(null);

        JobPosting jobPosting = modelMapper.map(jobPostingDTO, JobPosting.class);
        jobPosting.setSkills(new HashSet<>(skills));
        jobPosting.setCategory(category);

        handleJobPostingQuestions(jobPostingDTO, jobPosting);

        if (jobPosting.getJobPostingStatus() == null) {
            jobPosting.setJobPostingStatus(JobPostingStatus.IN_REVIEW);
        }
        JobPosting savedJobPosting = jobPostingRepository.save(jobPosting);
        return mapJobPostingToDTO(savedJobPosting);
    }
    private JobPostingDTO mapJobPostingToDTO(JobPosting jobPosting) {
        JobPostingDTO dto = modelMapper.map(jobPosting, JobPostingDTO.class);

        if (jobPosting.getQuestions() != null) {
            List<JobPostingQuestionDTO> questionDTOs = jobPosting.getQuestions().stream()
                    .map(q -> JobPostingQuestionDTO.builder()
                            .questionId(q.getQuestionId() != null ? q.getQuestionId().longValue() : null)
                            .question(q.getQuestion())
                            .build())
                    .toList();
            dto.setQuestions(questionDTOs);
        }

        return dto;
    }
    private void handleJobPostingQuestions(JobPostingDTO jobPostingDTO, JobPosting jobPosting) {
        List<JobPostingQuestionDTO> questionDTOs = jobPostingDTO.getQuestions();
        if (questionDTOs != null && !questionDTOs.isEmpty()) {
            if (questionDTOs.size() > 5) {
                throw new QuestionLimitExceededException();
            }
            List<JobPostingQuestion> questions = questionDTOs.stream()
                    .map(dto -> JobPostingQuestion.builder()
                            .question(dto.getQuestion())
                            .jobPosting(jobPosting)
                            .build())
                    .toList();
            jobPosting.setQuestions(questions);
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
