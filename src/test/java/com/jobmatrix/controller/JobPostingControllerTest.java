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

        JobPostingDTO jobPostingDTO = JobPostingTestDataFactory.createDTO(clientId);
        JobPosting mockJobPosting = JobPostingTestDataFactory.createEntity();

        Mockito.when(jobPostingService.createJobPosting(Mockito.any(JobPostingDTO.class)))
                .thenReturn(mockJobPosting);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/job-postings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jobPostingDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.jobPostingId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.updatedAt").exists());
    }

    @Test
    void testCreateJobPostingWithInvalidData() throws Exception {
        JobPostingDTO invalidDTO = new JobPostingDTO(); // Missing required fields

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/job-postings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
