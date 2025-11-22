package com.jobmatrix.controller;

import com.common.dto.JobPostingDTO;
import com.common.dto.PresignedUrlResponseDTO;
import com.common.dto.SkillDTO;
import com.common.entity.JobPosting;
import com.common.enums.BudgetType;
import com.common.enums.ExperienceLevel;
import com.common.enums.JobPostingStatus;
import com.common.util.S3FileUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobmatrix.dto.JobPostingUpdateRequest;
import com.jobmatrix.service.JobPostingService;
import com.jobmatrix.test_utils.factory.JobPostingTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.net.URL;
import java.util.*;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(JobPostingController.class)
public class JobPostingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JobPostingService jobPostingService;

    @MockitoBean
    private ModelMapper modelMapper;

    @MockitoBean
    private S3FileUtil s3FileUtil;

    private PresignedUrlResponseDTO presignedUrlResponse;
    private Map<String, PresignedUrlResponseDTO> urlResponseMap;

    @BeforeEach
    void setup() throws Exception {
        URL uploadUrl = new URL("https://s3-upload-url");
        presignedUrlResponse = new PresignedUrlResponseDTO(uploadUrl, "job-attachments/1/resume.pdf");

        urlResponseMap = new HashMap<>();
        urlResponseMap.put("resume.pdf", presignedUrlResponse);
    }


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
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jobPostingDTO)))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.jobPostingId").exists());
    }

    @Test
    void testGetOpenJobPostings() throws Exception {
        UUID clientId = UUID.randomUUID();
        List<JobPostingDTO> mockJobPostings = List.of(
                JobPostingTestDataFactory.createJobPostingDTO(clientId),
                JobPostingTestDataFactory.createJobPostingDTO(clientId)
        );
        Mockito.when(jobPostingService.getOpenJobPostings()).thenReturn(mockJobPostings);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/job_postings/open_job_postings"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value("Sample Job"));
    }

    @Test
    void testGetJobPostingById() throws Exception {
        UUID clientId = UUID.randomUUID();
        long jobPostingId = 1L;

        Mockito.when(jobPostingService.getJobPostingById(jobPostingId)).thenReturn(null); // Controller returns nothing

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/job_postings/" + jobPostingId))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("")); // Expect empty body
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
                .andExpect(status().isOk())
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
                .andExpect(status().isOk())
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

        Mockito.when(jobPostingService.updateJobPosting(eq(jobPostingId), Mockito.any(JobPostingUpdateRequest.class)))
                .thenReturn(null); // Controller returns nothing

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/job_postings/" + jobPostingId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("")); // Expect empty body

        Mockito.verify(jobPostingService).updateJobPosting(eq(jobPostingId), Mockito.any(JobPostingUpdateRequest.class));
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
                eq(clientId),
                Mockito.argThat(list ->
                        list.containsAll(Arrays.asList(JobPostingStatus.DRAFT, JobPostingStatus.OPEN))
                )
        )).thenReturn(mockResult);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/job_postings/{clientId}/statuses/{statuses}",
                        clientId, statuses))
                .andExpect(status().isOk())
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
                eq(clientId),
                anyList()
        )).thenReturn(new HashMap<>());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/job_postings/{clientId}/statuses/{statuses}",
                        clientId, statuses))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());
    }

    @Test
    void testGetJobPostingsByStatuses_EmptyStatuses() throws Exception {
        UUID clientId = UUID.randomUUID();
        String statuses = " ";
        Mockito.when(jobPostingService.getJobPostingsByStatuses(
                eq(clientId),
                eq(Collections.emptyList())
        )).thenReturn(Collections.emptyMap());
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/job_postings/{clientId}/statuses/{statuses}",
                        clientId, statuses))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{}"));
    }

    @Test
    void testGetJobPostingsByStatuses_InvalidEnumValue() throws Exception {
        UUID clientId = UUID.randomUUID();
        String statuses = "DRAFT,INVALID_STATUS";

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/job_postings/{clientId}/statuses/{statuses}",
                        clientId, statuses))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Invalid value 'INVALID_STATUS' for enum: JobPostingStatus"));
    }

    @Test
    void testGetAllSkills_ReturnsListOfSkills() throws Exception {
        // Arrange
        List<SkillDTO> mockSkills = List.of(
                SkillDTO.builder().skillId(1L).skill("Java").build(),
                SkillDTO.builder().skillId(2L).skill("Spring Boot").build()
        );

        when(jobPostingService.getAllSkills()).thenReturn(mockSkills);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/job_postings/skills")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].skillId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].skill").value("Java"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].skillId").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].skill").value("Spring Boot"));

        verify(jobPostingService, times(1)).getAllSkills();
    }

    @Test
    void generateUploadUrlsForJobAttachments_returnsPresignedUrls() throws Exception {
        when(s3FileUtil.generateMultipleJobAttachmentUrls(anyString(), anySet(), anyString()))
                .thenReturn(urlResponseMap);

        String requestJson = "[\"resume.pdf\"]";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/job_postings/1/presigned_urls")
                        .contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.['resume.pdf'].s3Key")
                        .value(presignedUrlResponse.getS3Key()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.['resume.pdf'].uploadUrl")
                        .value(presignedUrlResponse.getUploadUrl().toString()));
    }

    @Test
    void addAttachmentsToJobPosting_savesAttachmentsAndReturnsOk() throws Exception {
        doNothing().when(jobPostingService).saveJobAttachments(anyLong(), anyList());

        String requestJson = "[\"job-attachments/1/resume.pdf\", \"job-attachments/1/cover.docx\"]";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/job_postings/1/attachments")
                        .contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());

        verify(jobPostingService, times(1)).saveJobAttachments(eq(1L), anyList());
    }


}
