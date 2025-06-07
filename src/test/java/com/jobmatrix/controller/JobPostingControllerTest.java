package com.jobmatrix.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobmatrix.dto.JobPostingDTO;
import com.jobmatrix.dto.JobPostingUpdateRequest;
import com.jobmatrix.entity.BudgetType;
import com.jobmatrix.entity.ExperienceLevel;
import com.jobmatrix.entity.JobPosting;
import com.jobmatrix.entity.JobPostingStatus;
import com.jobmatrix.service.JobPostingService;
import com.jobmatrix.test_utils.factory.JobPostingTestDataFactory;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.util.*;

import static org.hamcrest.Matchers.is;

@WebMvcTest(JobPostingController.class)
public class JobPostingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JobPostingService jobPostingService;

    @Test
    void testCreateJobPosting() throws Exception {
        UUID clientId = UUID.randomUUID();
        JobPostingDTO jobPostingDTO = JobPostingTestDataFactory.createJobPostingDTO(clientId);
        // Prepare mock DTO with non-null ID and timestamps
        JobPostingDTO mockJobPostingDTO = JobPostingTestDataFactory.createJobPostingDTO(clientId);
        mockJobPostingDTO.setJobPostingId(1L); // Set some non-null ID

        Mockito.when(jobPostingService.createJobPosting(Mockito.any(JobPostingDTO.class)))
                .thenReturn(mockJobPostingDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/job_postings/create_job_posting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jobPostingDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.jobPostingId").exists());
    }

    @Test
    void testGetOpenJobPostings() throws Exception {
        UUID clientId = UUID.randomUUID();
        List<JobPosting> mockJobPostings = List.of(
                JobPostingTestDataFactory.createJobPostingEntity(clientId),
                JobPostingTestDataFactory.createJobPostingEntity(clientId)
        );

        Mockito.when(jobPostingService.getOpenJobPostings()).thenReturn(mockJobPostings);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/job_postings/open_job_postings"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].jobPostingId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value("Sample Job"));
    }

    @Test
    void testGetJobPostingById() throws Exception {
        UUID clientId = UUID.randomUUID();
        long jobPostingId = 1L;

        JobPosting mockJobPosting = JobPostingTestDataFactory.createJobPostingEntity(clientId);

        Mockito.when(jobPostingService.getJobPostingById(jobPostingId)).thenReturn(mockJobPosting);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/job_postings/" + jobPostingId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.jobPostingId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Sample Job"));
    }

    @Test
    void testGetJobPostingsByClientId() throws Exception {
        UUID clientId = UUID.randomUUID();
        List<JobPosting> mockJobPostings = List.of(
                JobPostingTestDataFactory.createJobPostingEntity(clientId, 1L, "Sample Job 1", "Description 1"),
                JobPostingTestDataFactory.createJobPostingEntity(clientId, 2L, "Sample Job 2", "Description 2")
        );

        Mockito.when(jobPostingService.getJobPostingsByClientId(clientId)).thenReturn(mockJobPostings);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/job_postings/client/" + clientId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].jobPostingId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value("Sample Job 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].jobPostingId").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].title").value("Sample Job 2"));
    }

    @Test
    void testGetCategories() throws Exception {
        // Given
        Map<String, List<String>> mockCategories = Map.of(
                "Technology", List.of("Software Development", "DevOps"),
                "Design", List.of("UI/UX", "Graphic Design")
        );
        Mockito.when(jobPostingService.getCategories()).thenReturn(mockCategories);
        // When
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/job_postings/categories"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.Technology.length()", is(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.Technology[0]", is("Software Development")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.Technology[1]", is("DevOps")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.Design.length()", is(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.Design[0]", is("UI/UX")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.Design[1]", is("Graphic Design")));
    }


    @Test
    void testUpdateJobPostingTest() throws Exception {
        // Create base entity with initial values
        JobPosting updatedJobPostingEntity = JobPostingTestDataFactory.createJobPostingEntity(UUID.randomUUID());

        // Update entity with new values
        updatedJobPostingEntity = updatedJobPostingEntity.toBuilder()
                .title("Updated Job Title")
                .description("Updated job description")
                .budgetType(BudgetType.FIXED)
                .fixedPrice(5000)
                .hourlyMinRate(0)
                .hourlyMaxRate(0)
                .experienceLevel(ExperienceLevel.BEGINNER)
                .category(JobPostingTestDataFactory.createMockCategory(3L, "Updated Category", "Updated Speciality"))
                .build();

        // Create and update the request
        JobPostingUpdateRequest updateRequest = JobPostingTestDataFactory.createJobPostingUpdateRequest();

        updateRequest = updateRequest.toBuilder()
                .title("Updated Job Title")
                .description("Updated job description")
                .budgetType(BudgetType.FIXED)
                .fixedPrice(5000)
                .hourlyMinRate(0)
                .hourlyMaxRate(0)
                .experienceLevel(ExperienceLevel.BEGINNER)
                .categoryId(3L)
                .build();

        Long jobPostingId = 1L;

        Mockito.when(jobPostingService.updateJobPosting(Mockito.eq(jobPostingId), Mockito.any(JobPostingUpdateRequest.class)))
                .thenReturn(updatedJobPostingEntity);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/job_postings/" + jobPostingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.jobPostingId").value(jobPostingId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Updated Job Title"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Updated job description"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.budgetType").value("FIXED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fixedPrice").value(5000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hourlyMinRate").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hourlyMaxRate").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.experienceLevel").value("BEGINNER"))
                // verify other fields remain unchanged
                .andExpect(MockMvcResultMatchers.jsonPath("$.projectDuration").value(updatedJobPostingEntity.getProjectDuration().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.jobPostingStatus").value(updatedJobPostingEntity.getJobPostingStatus().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.category.categoryId").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.category.category").value("Updated Category"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.category.speciality").value("Updated Speciality"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.updatedAt").exists());

        Mockito.verify(jobPostingService).updateJobPosting(Mockito.eq(jobPostingId), Mockito.any(JobPostingUpdateRequest.class));
    }

    @Test
    void testGetJobPostingsByStatuses() throws Exception {
        UUID clientId = UUID.randomUUID();
        String statuses = "DRAFT,OPEN";

        JobPostingDTO draftJobDTO = JobPostingTestDataFactory.createJobPostingDTO(clientId);
        JobPostingDTO openJobDTO = JobPostingTestDataFactory.createJobPostingDTO(clientId);
        openJobDTO.setTitle("Open Job");

        Map<JobPostingStatus, List<JobPostingDTO>> mockResult = Map.of(
                JobPostingStatus.DRAFT, List.of(draftJobDTO),
                JobPostingStatus.OPEN, List.of(openJobDTO)
        );

        Mockito.when(jobPostingService.getJobPostingsByStatuses(
                Mockito.eq(clientId),
                Mockito.argThat(list ->
                        list.containsAll(Arrays.asList(JobPostingStatus.DRAFT, JobPostingStatus.OPEN))
                )
        )).thenReturn(mockResult);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/job_postings/{clientId}/statuses/{statuses}",
                        clientId, statuses))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.DRAFT").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.OPEN").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.DRAFT.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.OPEN.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.DRAFT[0].title").value(draftJobDTO.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.OPEN[0].title").value("Open Job"));
    }

    @Test
    void testGetJobPostingsByStatuses_EmptyResult() throws Exception {
        UUID clientId = UUID.randomUUID();
        String statuses = "DRAFT,OPEN";

        Mockito.when(jobPostingService.getJobPostingsByStatuses(
                Mockito.eq(clientId),
                Mockito.anyList()
        )).thenReturn(new HashMap<>());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/job_postings/{clientId}/statuses/{statuses}",
                        clientId, statuses))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());
    }

    @Test
    void testGetJobPostingsByStatuses_EmptyStatuses() throws Exception {
        UUID clientId = UUID.randomUUID();
        String statuses = " ";
        Mockito.when(jobPostingService.getJobPostingsByStatuses(
                Mockito.eq(clientId),
                Mockito.eq(Collections.emptyList())
        )).thenReturn(Collections.emptyMap());
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/job_postings/{clientId}/statuses/{statuses}",
                        clientId, statuses))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{}"));
    }

    @Test
    void testGetJobPostingsByStatuses_InvalidEnumValue() throws Exception {
        UUID clientId = UUID.randomUUID();
        String statuses = "DRAFT,INVALID_STATUS";

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/job_postings/{clientId}/statuses/{statuses}",
                        clientId, statuses))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Invalid value 'INVALID_STATUS' for enum: JobPostingStatus"));
    }
}