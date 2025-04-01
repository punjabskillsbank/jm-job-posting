package com.jobmatrix.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobmatrix.BaseTest;
import com.jobmatrix.dto.JobPostingDTO;
import com.jobmatrix.entity.JobPosting;
import com.jobmatrix.entity.BudgetType;
import com.jobmatrix.entity.ProjectDuration;
import com.jobmatrix.entity.ExperienceLevel;
import com.jobmatrix.service.JobPostingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(JobPostingController.class)
public class JobPostingControllerTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JobPostingService jobPostingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateJobPosting() throws Exception {
        // Given
        JobPostingDTO jobPostingDTO = new JobPostingDTO();
        jobPostingDTO.setClientId(UUID.randomUUID());
        jobPostingDTO.setTitle("Test Job");
        jobPostingDTO.setDescription("Test Description");
        jobPostingDTO.setBudgetType(BudgetType.HOURLY);
        jobPostingDTO.setHourlyMinRate(50);
        jobPostingDTO.setHourlyMaxRate(100);
        jobPostingDTO.setProjectDuration(ProjectDuration.SHORT_TERM);
        jobPostingDTO.setExperienceLevel(ExperienceLevel.BEGINNER);
        jobPostingDTO.setCategoryId(1L);

        JobPosting mockJobPosting = new JobPosting();
        mockJobPosting.setJobPostingId(1L);
        mockJobPosting.setCreatedAt(LocalDateTime.now());
        mockJobPosting.setUpdatedAt(LocalDateTime.now());

        when(jobPostingService.createJobPosting(any(JobPostingDTO.class))).thenReturn(mockJobPosting);

        // When/Then
        mockMvc.perform(post("/api/v1/job-postings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(jobPostingDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.jobPostingId").value(1))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    public void testCreateJobPostingWithInvalidData() throws Exception {
        // Given
        JobPostingDTO jobPostingDTO = new JobPostingDTO();
        // Missing required fields

        // When/Then
        mockMvc.perform(post("/api/v1/job-postings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(jobPostingDTO)))
                .andExpect(status().isBadRequest());
    }
} 