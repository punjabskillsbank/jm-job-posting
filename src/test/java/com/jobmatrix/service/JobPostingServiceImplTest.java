package com.jobmatrix.service;

import com.common.dto.JobPostingDTO;
import com.common.dto.JobPostingQuestionDTO;
import com.common.entity.Category;
import com.common.entity.JobPosting;
import com.common.entity.Skill;
import com.common.enums.BudgetType;
import com.common.enums.ExperienceLevel;
import com.common.enums.JobPostingStatus;
import com.common.enums.ProjectDuration;
import com.common.exceptionHandling.ClientNotFoundException;
import com.common.exceptionHandling.JobPostingNotFoundException;
import com.jobmatrix.dto.JobPostingUpdateRequest;
import com.common.dto.CategoryDTO;
import com.common.dto.SkillDTO;
import com.common.entity.Client;
import com.jobmatrix.exceptionHandling.CategoryNotFoundException;
import com.jobmatrix.repository.CategoryRepository;
import com.jobmatrix.repository.ClientRepository;
import com.jobmatrix.repository.SkillRepository;
import com.jobmatrix.repository.JobPostingRepository;
import com.jobmatrix.serviceimpl.JobPostingServiceImpl;
import com.jobmatrix.test_utils.factory.JobPostingTestDataFactory;
import com.jobmatrix.exceptionHandling.QuestionLimitExceededException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JobPostingServiceImplTest {

    @Mock
    private SkillRepository skillRepository;


    @Mock
    private JobPostingRepository jobPostingRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private JobPostingServiceImpl jobPostingService;

    private JobPostingDTO jobPostingDTO;
    private Category category;

    @BeforeEach
    public void setUp() {
        UUID clientId = UUID.randomUUID();
        jobPostingDTO = JobPostingTestDataFactory.createJobPostingDTO(clientId);

        category = new Category();
        category.setCategoryId(1L); // Provide a test ID
        category.setCategory("Test Category");
        category.setSpeciality("Test Speciality");
    }

    @Test
    public void testCreateJobPosting_Success() {
        // Prepare input DTO
        Set<SkillDTO> skillDTOs = Set.of(
                SkillDTO.builder().skillId(101L).skill("Java").build(),
                SkillDTO.builder().skillId(102L).skill("Spring Boot").build()
        );

        CategoryDTO categoryDTO = CategoryDTO.builder()
                .categoryId(1L)
                .category("Development")
                .speciality("Backend")
                .build();

        jobPostingDTO.setTitle("Java Developer");
        jobPostingDTO.setDescription("Experienced dev needed");
        jobPostingDTO.setBudgetType(BudgetType.HOURLY);
        jobPostingDTO.setHourlyMinRate(50);
        jobPostingDTO.setHourlyMaxRate(100);
        jobPostingDTO.setProjectDuration(ProjectDuration.SHORT_TERM);
        jobPostingDTO.setExperienceLevel(ExperienceLevel.INTERMEDIATE);
        jobPostingDTO.setCategory(categoryDTO);
        jobPostingDTO.setSkills(skillDTOs);

        // Mock Skill entities
        Skill skill1 = new Skill();
        skill1.setSkillId(101L);
        skill1.setSkill("Java");

        Skill skill2 = new Skill();
        skill2.setSkillId(102L);
        skill2.setSkill("Spring Boot");

        // Mock Category entity
        Category category = new Category();
        category.setCategoryId(1L);
        category.setCategory("Development");
        category.setSpeciality("Backend");

        // Mock JobPosting entity
        JobPosting jobPosting = new JobPosting();
        jobPosting.setTitle(jobPostingDTO.getTitle());
        jobPosting.setDescription(jobPostingDTO.getDescription());
        jobPosting.setBudgetType(jobPostingDTO.getBudgetType());
        jobPosting.setHourlyMinRate(jobPostingDTO.getHourlyMinRate());
        jobPosting.setHourlyMaxRate(jobPostingDTO.getHourlyMaxRate());
        jobPosting.setProjectDuration(jobPostingDTO.getProjectDuration());
        jobPosting.setExperienceLevel(jobPostingDTO.getExperienceLevel());
        jobPosting.setCategory(category);
        jobPosting.setSkills(Set.of(skill1, skill2));
        jobPosting.setJobPostingStatus(JobPostingStatus.OPEN);

        // Mock repository and mapper behavior
        when(skillRepository.findAllById(any())).thenReturn(List.of(skill1, skill2));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(modelMapper.map(jobPostingDTO, JobPosting.class)).thenReturn(jobPosting);
        when(jobPostingRepository.save(any(JobPosting.class))).thenAnswer(invocation -> {
            JobPosting saved = invocation.getArgument(0);
            saved.setJobPostingId(1L);
            return saved;
        });

        JobPostingDTO returnedDTO = JobPostingDTO.builder()
                .title(jobPosting.getTitle())
                .description(jobPosting.getDescription())
                .budgetType(jobPosting.getBudgetType())
                .hourlyMinRate(jobPosting.getHourlyMinRate())
                .hourlyMaxRate(jobPosting.getHourlyMaxRate())
                .projectDuration(jobPosting.getProjectDuration())
                .experienceLevel(jobPosting.getExperienceLevel())
                .jobPostingStatus(jobPosting.getJobPostingStatus())
                .category(categoryDTO)
                .skills(skillDTOs)
                .build();

        when(modelMapper.map(any(JobPosting.class), eq(JobPostingDTO.class))).thenReturn(returnedDTO);

        // Call service method
        JobPostingDTO result = jobPostingService.createJobPosting(jobPostingDTO);

        // Assertions
        assertNotNull(result);
        assertEquals(jobPostingDTO.getTitle(), result.getTitle());
        assertEquals(jobPostingDTO.getDescription(), result.getDescription());
        assertEquals(jobPostingDTO.getBudgetType(), result.getBudgetType());
        assertEquals(jobPostingDTO.getHourlyMinRate(), result.getHourlyMinRate());
        assertEquals(jobPostingDTO.getHourlyMaxRate(), result.getHourlyMaxRate());
        assertEquals(jobPostingDTO.getProjectDuration(), result.getProjectDuration());
        assertEquals(jobPostingDTO.getExperienceLevel(), result.getExperienceLevel());
        assertEquals(JobPostingStatus.OPEN, result.getJobPostingStatus());
        assertEquals(jobPostingDTO.getCategory().getCategoryId(), result.getCategory().getCategoryId());

        Set<Long> expectedSkillIds = jobPostingDTO.getSkills().stream()
                .map(SkillDTO::getSkillId)
                .collect(Collectors.toSet());
        Set<Long> resultSkillIds = result.getSkills().stream()
                .map(SkillDTO::getSkillId)
                .collect(Collectors.toSet());

        assertEquals(expectedSkillIds, resultSkillIds);

        // Verify repository calls with proper argument matcher
        verify(skillRepository, times(1)).findAllById(argThat(ids -> {
            Set<Long> idsSet = new HashSet<>();
            ids.forEach(idsSet::add);
            return idsSet.equals(expectedSkillIds);
        }));
        verify(categoryRepository, times(1)).findById(1L);
        verify(jobPostingRepository, times(1)).save(any(JobPosting.class));
        verify(modelMapper, times(1)).map(jobPostingDTO, JobPosting.class);
        verify(modelMapper, times(1)).map(any(JobPosting.class), eq(JobPostingDTO.class));
    }


    @Test
    public void testCreateJobPosting_NullStatus() {
        // Prepare JobPosting entity mapped from DTO
        JobPosting jobPosting = new JobPosting();
        jobPosting.setTitle(jobPostingDTO.getTitle());
        jobPosting.setDescription(jobPostingDTO.getDescription());
        jobPosting.setBudgetType(jobPostingDTO.getBudgetType());
        jobPosting.setHourlyMinRate(jobPostingDTO.getHourlyMinRate());
        jobPosting.setHourlyMaxRate(jobPostingDTO.getHourlyMaxRate());
        jobPosting.setProjectDuration(jobPostingDTO.getProjectDuration());
        jobPosting.setExperienceLevel(jobPostingDTO.getExperienceLevel());

        // Set null status to trigger default in service
        jobPostingDTO.setJobPostingStatus(null);

        // Prepare skill IDs and mock skill repository
        Set<Long> skillIds = Set.of(101L, 102L);
        Set<SkillDTO> skillDTOs = skillIds.stream()
                .map(id -> SkillDTO.builder().skillId(id).build())
                .collect(Collectors.toSet());
        jobPostingDTO.setSkills(skillDTOs);

        Skill skill1 = new Skill();
        skill1.setSkillId(101L);
        skill1.setSkill("Skill 1");

        Skill skill2 = new Skill();
        skill2.setSkillId(102L);
        skill2.setSkill("Skill 2");

        // Use argThat to match skillIds ignoring order
        when(skillRepository.findAllById(argThat(ids -> {
            if (ids == null) return false;
            Set<Long> actualIds = new HashSet<>();
            ids.forEach(actualIds::add);
            return actualIds.equals(skillIds);
        }))).thenReturn(List.of(skill1, skill2));

        when(categoryRepository.findById(jobPostingDTO.getCategory().getCategoryId())).thenReturn(Optional.of(category));
        when(modelMapper.map(jobPostingDTO, JobPosting.class)).thenReturn(jobPosting);

        // Mock save to set ID, timestamps, and default status if null
        when(jobPostingRepository.save(any(JobPosting.class))).thenAnswer(invocation -> {
            JobPosting savedJob = invocation.getArgument(0);
            savedJob.setJobPostingId(1L);
            savedJob.setCreatedAt(LocalDateTime.now());
            savedJob.setUpdatedAt(LocalDateTime.now());
            // Simulate default status set by service if null
            if (savedJob.getJobPostingStatus() == null) {
                savedJob.setJobPostingStatus(JobPostingStatus.IN_REVIEW);
            }
            return savedJob;
        });

        // Mock mapping back to DTO
        JobPostingDTO returnedDTO = new JobPostingDTO();
        returnedDTO.setJobPostingStatus(JobPostingStatus.IN_REVIEW);

        when(modelMapper.map(any(JobPosting.class), eq(JobPostingDTO.class)))
                .thenReturn(returnedDTO);

        // Call service
        JobPostingDTO result = jobPostingService.createJobPosting(jobPostingDTO);

        // Assertions
        assertNotNull(result);
        assertEquals(JobPostingStatus.IN_REVIEW, result.getJobPostingStatus());
    }

    @Test
    public void testCreateJobPosting_WithQuestions_Success() {
        // Prepare skill and category DTOs
        Set<SkillDTO> skillDTOs = Set.of(
                SkillDTO.builder().skillId(201L).skill("Python").build()
        );
        CategoryDTO categoryDTO = CategoryDTO.builder()
                .categoryId(2L)
                .category("Data Science")
                .speciality("ML")
                .build();

        // Prepare questions
        List<JobPostingQuestionDTO> questionDTOs = List.of(
                JobPostingQuestionDTO.builder().question("What is your ML experience?").build(),
                JobPostingQuestionDTO.builder().question("Do you have experience with Pandas?").build()
        );

        jobPostingDTO.setSkills(skillDTOs);
        jobPostingDTO.setCategory(categoryDTO);
        jobPostingDTO.setQuestions(questionDTOs);

        // Mock skill entities
        Skill skill = new Skill();
        skill.setSkillId(201L);
        skill.setSkill("Python");

        // Mock category
        Category category = new Category();
        category.setCategoryId(2L);
        category.setCategory("Data Science");
        category.setSpeciality("ML");

        // Mock JobPosting entity
        JobPosting jobPosting = new JobPosting();
        jobPosting.setTitle(jobPostingDTO.getTitle());
        jobPosting.setDescription(jobPostingDTO.getDescription());
        jobPosting.setBudgetType(jobPostingDTO.getBudgetType());
        jobPosting.setHourlyMinRate(jobPostingDTO.getHourlyMinRate());
        jobPosting.setHourlyMaxRate(jobPostingDTO.getHourlyMaxRate());
        jobPosting.setProjectDuration(jobPostingDTO.getProjectDuration());
        jobPosting.setExperienceLevel(jobPostingDTO.getExperienceLevel());
        jobPosting.setSkills(Set.of(skill));
        jobPosting.setCategory(category);

        // Mock repositories and mapper
        when(skillRepository.findAllById(any())).thenReturn(List.of(skill));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));
        when(modelMapper.map(jobPostingDTO, JobPosting.class)).thenReturn(jobPosting);

        when(jobPostingRepository.save(any(JobPosting.class))).thenAnswer(invocation -> {
            JobPosting saved = invocation.getArgument(0);
            saved.setJobPostingId(5L);
            saved.setQuestions(saved.getQuestions()); // questions already set by service
            return saved;
        });

        JobPostingDTO returnedDTO = JobPostingDTO.builder()
                .title(jobPosting.getTitle())
                .description(jobPosting.getDescription())
                .budgetType(jobPosting.getBudgetType())
                .hourlyMinRate(jobPosting.getHourlyMinRate())
                .hourlyMaxRate(jobPosting.getHourlyMaxRate())
                .projectDuration(jobPosting.getProjectDuration())
                .experienceLevel(jobPosting.getExperienceLevel())
                .jobPostingStatus(JobPostingStatus.IN_REVIEW)
                .category(categoryDTO)
                .skills(skillDTOs)
                .questions(questionDTOs)
                .build();

        when(modelMapper.map(any(JobPosting.class), eq(JobPostingDTO.class))).thenReturn(returnedDTO);

        // Call the service method
        JobPostingDTO result = jobPostingService.createJobPosting(jobPostingDTO);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getQuestions().size());
        assertEquals("What is your ML experience?", result.getQuestions().get(0).getQuestion());
        assertEquals("Do you have experience with Pandas?", result.getQuestions().get(1).getQuestion());

        verify(skillRepository, times(1)).findAllById(any());
        verify(categoryRepository, times(1)).findById(2L);
        verify(jobPostingRepository, times(1)).save(any(JobPosting.class));
    }

    @Test
    public void testCreateJobPosting_WithMoreThanFiveQuestions_ThrowsException() {
        List<JobPostingQuestionDTO> questionDTOs = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            questionDTOs.add(JobPostingQuestionDTO.builder().question("Question " + i).build());
        }
        Set<SkillDTO> skillDTOs = Set.of(SkillDTO.builder().skillId(301L).skill("Node.js").build());
        CategoryDTO categoryDTO = CategoryDTO.builder().categoryId(3L).category("Web Dev").speciality("Backend").build();
        jobPostingDTO.setSkills(skillDTOs);
        jobPostingDTO.setCategory(categoryDTO);
        jobPostingDTO.setQuestions(questionDTOs);
        Skill skill = new Skill();
        skill.setSkillId(301L);
        skill.setSkill("Node.js");
        Category category = new Category();
        category.setCategoryId(3L);
        category.setCategory("Web Dev");
        category.setSpeciality("Backend");
        when(skillRepository.findAllById(any())).thenReturn(List.of(skill));
        when(categoryRepository.findById(3L)).thenReturn(Optional.of(category));
        when(modelMapper.map(any(JobPostingDTO.class), eq(JobPosting.class)))
                .thenReturn(new JobPosting()); // Fixed: return dummy JobPosting
        assertThrows(QuestionLimitExceededException.class, () -> {
            jobPostingService.createJobPosting(jobPostingDTO);
        });
        verify(jobPostingRepository, never()).save(any());
    }


    @Test
    public void testGetOpenJobPostings_WhenSomeAreOpen() {
        JobPosting openJob1 = JobPostingTestDataFactory.createJobPostingEntity(UUID.randomUUID());
        JobPosting openJob2 = JobPostingTestDataFactory.createJobPostingEntity(UUID.randomUUID());
        openJob1.setJobPostingStatus(JobPostingStatus.OPEN);
        openJob2.setJobPostingStatus(JobPostingStatus.OPEN);
        when(jobPostingRepository.findByJobPostingStatus(JobPostingStatus.OPEN))
                .thenReturn(List.of(openJob1, openJob2));
        List<JobPosting> result = jobPostingService.getOpenJobPostings();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(job -> job.getJobPostingStatus() == JobPostingStatus.OPEN));

        verify(jobPostingRepository).findByJobPostingStatus(JobPostingStatus.OPEN);
    }

    @Test
    public void testGetOpenJobPostings_WhenNoneAreOpen() {
        when(jobPostingRepository.findByJobPostingStatus(JobPostingStatus.OPEN))
                .thenReturn(List.of());

        List<JobPosting> result = jobPostingService.getOpenJobPostings();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(jobPostingRepository).findByJobPostingStatus(JobPostingStatus.OPEN);
    }

    @Test
    public void testGetOpenJobPostings_WithMultipleStatuses_ShouldReturnOnlyOpenJobs() {
        JobPosting openJob1 = JobPostingTestDataFactory.createJobPostingEntity(UUID.randomUUID());
        JobPosting openJob2 = JobPostingTestDataFactory.createJobPostingEntity(UUID.randomUUID());
        JobPosting draftJob = JobPostingTestDataFactory.createJobPostingEntity(UUID.randomUUID());
        JobPosting inReviewJob = JobPostingTestDataFactory.createJobPostingEntity(UUID.randomUUID());
        JobPosting inProgressJob = JobPostingTestDataFactory.createJobPostingEntity(UUID.randomUUID());
        JobPosting closedJob = JobPostingTestDataFactory.createJobPostingEntity(UUID.randomUUID());
        JobPosting completedJob = JobPostingTestDataFactory.createJobPostingEntity(UUID.randomUUID());

        openJob1.setJobPostingStatus(JobPostingStatus.OPEN);
        openJob2.setJobPostingStatus(JobPostingStatus.OPEN);
        draftJob.setJobPostingStatus(JobPostingStatus.DRAFT);
        inReviewJob.setJobPostingStatus(JobPostingStatus.IN_REVIEW);
        inProgressJob.setJobPostingStatus(JobPostingStatus.IN_PROGRESS);
        closedJob.setJobPostingStatus(JobPostingStatus.CLOSED);
        completedJob.setJobPostingStatus(JobPostingStatus.COMPLETED);

        when(jobPostingRepository.findByJobPostingStatus(JobPostingStatus.OPEN))
                .thenReturn(List.of(openJob1, openJob2));

        List<JobPosting> result = jobPostingService.getOpenJobPostings();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(job -> job.getJobPostingStatus() == JobPostingStatus.OPEN));

        verify(jobPostingRepository, times(1)).findByJobPostingStatus(JobPostingStatus.OPEN);
    }

    @Test
    void getJobPostingById_shouldReturnJobPostingEntity() {
        long jobPostingId = 1L;
        JobPosting mockJobPosting = JobPostingTestDataFactory.createJobPostingEntity(UUID.randomUUID());
        mockJobPosting.setJobPostingId(jobPostingId);

        when(jobPostingRepository.findById(jobPostingId)).thenReturn(Optional.of(mockJobPosting));

        JobPosting result = jobPostingService.getJobPostingById(jobPostingId);

        assertNotNull(result);
        assertEquals(mockJobPosting.getJobPostingId(), result.getJobPostingId());
        assertEquals(mockJobPosting.getTitle(), result.getTitle());
        assertEquals(mockJobPosting.getDescription(), result.getDescription());
        assertEquals(mockJobPosting.getBudgetType(), result.getBudgetType());
        assertEquals(mockJobPosting.getHourlyMinRate(), result.getHourlyMinRate());
        assertEquals(mockJobPosting.getHourlyMaxRate(), result.getHourlyMaxRate());
        assertEquals(mockJobPosting.getProjectDuration(), result.getProjectDuration());
        assertEquals(mockJobPosting.getExperienceLevel(), result.getExperienceLevel());

        verify(jobPostingRepository, times(1)).findById(jobPostingId);
    }

    @Test
    void getJobPostingById_shouldThrowJobPostingNotFoundException() {
        long jobPostingId = 2L;

        when(jobPostingRepository.findById(jobPostingId)).thenReturn(Optional.empty());

        JobPostingNotFoundException exception = assertThrows(
                JobPostingNotFoundException.class,
                () -> jobPostingService.getJobPostingById(jobPostingId),
                "JobPosting not found at given jobPostingId"
        );

        assertEquals("JobPosting not found at given jobPostingId: " + jobPostingId, exception.getMessage());
        verify(jobPostingRepository, times(1)).findById(jobPostingId);
    }

    @Test
    void getJobPostingsByClientId_shouldReturnJobPostingsForExistingClient() {
        UUID clientId = UUID.randomUUID();
        JobPosting mockJobPosting1 = JobPostingTestDataFactory.createJobPostingEntity(clientId);
        JobPosting mockJobPosting2 = JobPostingTestDataFactory.createJobPostingEntity(clientId);

        // Mock client existence
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(new Client()));
        // Mock job postings
        when(jobPostingRepository.findByClientId(clientId)).thenReturn(List.of(mockJobPosting1, mockJobPosting2));

        List<JobPosting> result = jobPostingService.getJobPostingsByClientId(clientId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(mockJobPosting1.getClientId(), result.get(0).getClientId());
        assertEquals(mockJobPosting2.getClientId(), result.get(1).getClientId());

        verify(clientRepository, times(1)).findById(clientId);
        verify(jobPostingRepository, times(1)).findByClientId(clientId);
    }

    @Test
    void getJobPostingsByClientId_shouldThrowClientNotFoundException() {
        UUID clientId = UUID.randomUUID();

        // Mock client non-existence
        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        ClientNotFoundException exception = assertThrows(
                ClientNotFoundException.class,
                () -> jobPostingService.getJobPostingsByClientId(clientId),
                "Client not found with ID"
        );

        assertEquals("Client not found with ID: " + clientId, exception.getMessage());
        verify(clientRepository, times(1)).findById(clientId);
    }

    @Test
    void getJobPostingsByClientId_shouldReturnEmptyListWhenNoPostingsExist() {
        UUID clientId = UUID.randomUUID();
        // Mock client existence
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(new Client()));
        // Mock empty job postings
        when(jobPostingRepository.findByClientId(clientId)).thenReturn(List.of());

        // Call the method
        List<JobPosting> result = jobPostingService.getJobPostingsByClientId(clientId);

        // Assert result is empty
        assertTrue(result.isEmpty());

        // Verify interactions
        verify(clientRepository, times(1)).findById(clientId);
        verify(jobPostingRepository, times(1)).findByClientId(clientId);
    }


    @Test
    void testGetCategories_ShouldReturnMapOfCategories() {
        Category category1 = JobPostingTestDataFactory.createMockCategory(3L, "Sample Category", "Sample Speciality");
        Category category2 = JobPostingTestDataFactory.createMockCategory(1L, "Test Category", "Test Speciality");
        List<Category> mockCategories = List.of(category1, category2);
        Map<String, List<String>> groupedCategories = mockCategories.stream()
                .collect(Collectors.groupingBy(
                        Category::getCategory,
                        Collectors.mapping(Category::getSpeciality, Collectors.toList())
                ));
        when(categoryRepository.findAll()).thenReturn(mockCategories);

        Map<String, List<String>> result = jobPostingService.getCategories();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.containsKey("Sample Category"));
        assertTrue(result.containsKey("Test Category"));
        assertEquals(List.of("Sample Speciality"), result.get("Sample Category"));
        assertEquals(List.of("Test Speciality"), result.get("Test Category"));
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void updateJobPosting_shouldUpdateAndReturnJobPosting() {
        Long jobPostingId = 1L;
        UUID clientId = UUID.randomUUID();
        Long categoryId = 1L;

        JobPosting existingJobPosting = JobPostingTestDataFactory.createJobPostingEntity(clientId);
        JobPostingUpdateRequest updateRequest = JobPostingTestDataFactory.createJobPostingUpdateRequest();
        Category updatedCategory = JobPostingTestDataFactory.createMockCategory(categoryId, "Updated Category", "Updated Speciality");

        JobPosting updatedJobPosting = JobPostingTestDataFactory.createUpdatedJobPostingEntity(
                jobPostingId, clientId, updateRequest, updatedCategory, existingJobPosting.getCreatedAt());

        when(jobPostingRepository.findById(jobPostingId)).thenReturn(Optional.of(existingJobPosting));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(updatedCategory));

        org.modelmapper.config.Configuration mockConfig = mock(org.modelmapper.config.Configuration.class);
        when(modelMapper.getConfiguration()).thenReturn(mockConfig);

        doAnswer(invocation -> {
            JobPostingUpdateRequest src = invocation.getArgument(0);
            JobPosting dest = invocation.getArgument(1);
            dest.setTitle(src.getTitle());
            dest.setDescription(src.getDescription());
            dest.setHourlyMinRate(src.getHourlyMinRate());
            dest.setHourlyMaxRate(src.getHourlyMaxRate());
            dest.setProjectDuration(src.getProjectDuration());
            dest.setExperienceLevel(src.getExperienceLevel());
            return null;
        }).when(modelMapper).map(updateRequest, existingJobPosting);

        when(jobPostingRepository.save(existingJobPosting)).thenReturn(updatedJobPosting);

        JobPosting result = jobPostingService.updateJobPosting(jobPostingId, updateRequest);

        assertNotNull(result);
        assertEquals(updateRequest.getTitle(), result.getTitle());
        assertEquals(updateRequest.getDescription(), result.getDescription());
        assertEquals(updatedCategory, result.getCategory());

        verify(jobPostingRepository, times(1)).findById(jobPostingId);
        verify(modelMapper, times(1)).getConfiguration();
        verify(mockConfig, times(1)).setSkipNullEnabled(true);
        verify(modelMapper, times(1)).map(updateRequest, existingJobPosting);
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(jobPostingRepository, times(1)).save(existingJobPosting);
    }

    @Test
    void updateJobPosting_shouldThrowException_whenJobPostingNotFound() {
        Long jobPostingId = 99L;
        JobPostingUpdateRequest updateRequest = JobPostingTestDataFactory.createJobPostingUpdateRequest();

        when(jobPostingRepository.findById(jobPostingId)).thenReturn(Optional.empty());

        assertThrows(JobPostingNotFoundException.class,
                () -> jobPostingService.updateJobPosting(jobPostingId, updateRequest));

        verify(jobPostingRepository, times(1)).findById(jobPostingId);
        verify(modelMapper, never()).map(any(), any());
        verify(jobPostingRepository, never()).save(any());
    }

    @Test
    void updateJobPosting_shouldThrowException_whenCategoryNotFound() {
        Long jobPostingId = 1L;
        UUID clientId = UUID.randomUUID();
        JobPosting existingJobPosting = JobPostingTestDataFactory.createJobPostingEntity(clientId);
        JobPostingUpdateRequest updateRequest = JobPostingTestDataFactory.createJobPostingUpdateRequest();

        when(jobPostingRepository.findById(jobPostingId)).thenReturn(Optional.of(existingJobPosting));
        when(categoryRepository.findById(updateRequest.getCategoryId())).thenReturn(Optional.empty());

        org.modelmapper.config.Configuration mockConfig = mock(org.modelmapper.config.Configuration.class);
        when(modelMapper.getConfiguration()).thenReturn(mockConfig);

        doAnswer(invocation -> null).when(modelMapper).map(updateRequest, existingJobPosting);

        assertThrows(CategoryNotFoundException.class,
                () -> jobPostingService.updateJobPosting(jobPostingId, updateRequest));

        verify(jobPostingRepository, times(1)).findById(jobPostingId);
        verify(categoryRepository, times(1)).findById(updateRequest.getCategoryId());
        verify(jobPostingRepository, never()).save(any());
    }

    @Test
    void getJobPostingsByStatuses_Success() {
        UUID clientId = UUID.randomUUID();
        List<JobPostingStatus> statusList = List.of(JobPostingStatus.DRAFT, JobPostingStatus.OPEN);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(mock(Client.class)));

        JobPosting draftPosting = JobPostingTestDataFactory.createJobPostingEntity(clientId);
        draftPosting.setJobPostingStatus(JobPostingStatus.DRAFT);

        JobPosting openPosting = JobPostingTestDataFactory.createJobPostingEntity(clientId);
        openPosting.setJobPostingStatus(JobPostingStatus.OPEN);

        when(jobPostingRepository.findByClientIdAndJobPostingStatus(clientId, JobPostingStatus.DRAFT))
                .thenReturn(List.of(draftPosting));
        when(jobPostingRepository.findByClientIdAndJobPostingStatus(clientId, JobPostingStatus.OPEN))
                .thenReturn(List.of(openPosting));

        JobPostingDTO draftDTO = JobPostingTestDataFactory.createJobPostingDTO(clientId);
        JobPostingDTO openDTO = JobPostingTestDataFactory.createJobPostingDTO(clientId);
        when(modelMapper.map(draftPosting, JobPostingDTO.class)).thenReturn(draftDTO);
        when(modelMapper.map(openPosting, JobPostingDTO.class)).thenReturn(openDTO);

        Map<JobPostingStatus, List<JobPostingDTO>> result = jobPostingService.getJobPostingsByStatuses(clientId, statusList);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.containsKey(JobPostingStatus.DRAFT));
        assertTrue(result.containsKey(JobPostingStatus.OPEN));
        assertEquals(1, result.get(JobPostingStatus.DRAFT).size());
        assertEquals(1, result.get(JobPostingStatus.OPEN).size());

        verify(jobPostingRepository).findByClientIdAndJobPostingStatus(clientId, JobPostingStatus.DRAFT);
        verify(jobPostingRepository).findByClientIdAndJobPostingStatus(clientId, JobPostingStatus.OPEN);
        verify(modelMapper, times(2)).map(any(JobPosting.class), eq(JobPostingDTO.class));
    }

    @Test
    void getJobPostingsByStatuses_EmptyResults() {
        UUID clientId = UUID.randomUUID();
        List<JobPostingStatus> statusList = List.of(JobPostingStatus.DRAFT, JobPostingStatus.OPEN);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(mock(Client.class)));

        when(jobPostingRepository.findByClientIdAndJobPostingStatus(eq(clientId), any(JobPostingStatus.class)))
                .thenReturn(List.of());

        Map<JobPostingStatus, List<JobPostingDTO>> result = jobPostingService.getJobPostingsByStatuses(clientId, statusList);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.containsKey(JobPostingStatus.DRAFT));
        assertTrue(result.containsKey(JobPostingStatus.OPEN));
        assertTrue(result.get(JobPostingStatus.DRAFT).isEmpty());
        assertTrue(result.get(JobPostingStatus.OPEN).isEmpty());

        verify(jobPostingRepository, times(2))
                .findByClientIdAndJobPostingStatus(eq(clientId), any(JobPostingStatus.class));
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void getJobPostingsByStatuses_WithEmptyStatusList() {
        UUID clientId = UUID.randomUUID();
        List<JobPostingStatus> statusList = List.of();

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(mock(Client.class)));

        Map<JobPostingStatus, List<JobPostingDTO>> result = jobPostingService.getJobPostingsByStatuses(clientId, statusList);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(jobPostingRepository, never()).findByClientIdAndJobPostingStatus(any(), any());
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void getJobPostingsByStatuses_shouldThrowClientNotFoundException() {
        UUID clientId = UUID.randomUUID();
        List<JobPostingStatus> statuses = List.of(JobPostingStatus.IN_PROGRESS);

        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        ClientNotFoundException exception = assertThrows(
                ClientNotFoundException.class,
                () -> jobPostingService.getJobPostingsByStatuses(clientId, statuses)
        );
        assertEquals("Client not found with ID: " + clientId, exception.getMessage());
        verify(clientRepository, times(1)).findById(clientId);
    }
}