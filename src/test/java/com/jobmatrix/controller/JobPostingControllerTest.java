package com.jobmatrix.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobmatrix.dto.JobPostingDTO;
import com.jobmatrix.entity.JobPosting;
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

import java.util.List;
import java.util.UUID;

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
        JobPosting mockJobPosting = JobPostingTestDataFactory.createJobPostingEntity(clientId);

        Mockito.when(jobPostingService.createJobPosting(Mockito.any(JobPostingDTO.class)))
                .thenReturn(mockJobPosting);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/job_postings/create_job_posting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jobPostingDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.jobPostingId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.updatedAt").exists());
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
                JobPostingTestDataFactory.createJobPostingEntity(clientId),
                JobPostingTestDataFactory.createJobPostingEntity(clientId)
        );

        Mockito.when(jobPostingService.getJobPostingsByClientId(clientId)).thenReturn(mockJobPostings);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/job_postings/client/" + clientId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].jobPostingId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value("Sample Job"));
    }

}
