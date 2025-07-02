package com.jobmatrix.controller;

import com.common.entity.JobPosting;
import com.common.exceptionHandling.JobPostingNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.jobmatrix.service.FreelancerSavedJobPostingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(FreelancerSavedJobPostingController.class)
public class FreelancerSavedJobPostingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FreelancerSavedJobPostingService savedJobService;

    @Autowired
    private ObjectMapper objectMapper;

    private final UUID freelancerId = UUID.randomUUID();
    private final Long jobPostingId = 1L;

    @Test
    void testSaveJobPosting_Returns200() throws Exception {
        mockMvc.perform(post("/api/freelancer/saved_job_postings/{freelancerId}/{jobPostingId}", freelancerId, jobPostingId))
                .andExpect(status().isOk());

        Mockito.verify(savedJobService).saveJobPosting(freelancerId, jobPostingId);
    }

    @Test
    void testSaveJobPosting_Returns404_WhenJobPostingNotFound() throws Exception {
        Mockito.doThrow(new JobPostingNotFoundException(jobPostingId))
                .when(savedJobService).saveJobPosting(freelancerId, jobPostingId);

        mockMvc.perform(post("/api/freelancer/saved_job_postings/{freelancerId}/{jobPostingId}", freelancerId, jobPostingId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("JobPosting not found at given jobPostingId: " + jobPostingId));

        Mockito.verify(savedJobService).saveJobPosting(freelancerId, jobPostingId);
    }

    @Test
    void testRemoveJobPosting_Returns200() throws Exception {
        mockMvc.perform(delete("/api/freelancer/saved_job_postings/{freelancerId}/{jobPostingId}", freelancerId, jobPostingId))
                .andExpect(status().isOk());

        Mockito.verify(savedJobService).removeSavedJobPosting(freelancerId, jobPostingId);
    }

    @Test
    void testGetSavedJobPostings_ReturnsList() throws Exception {
        JobPosting job1 = new JobPosting();
        job1.setJobPostingId(1L);
        job1.setTitle("Java Dev");

        JobPosting job2 = new JobPosting();
        job2.setJobPostingId(2L);
        job2.setTitle("Spring Boot Dev");

        Mockito.when(savedJobService.getSavedJobPostings(freelancerId))
                .thenReturn(List.of(job1, job2));

        mockMvc.perform(get("/api/freelancer/saved_job_postings/{freelancerId}", freelancerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].jobPostingId").value(1))
                .andExpect(jsonPath("$[0].title").value("Java Dev"))
                .andExpect(jsonPath("$[1].jobPostingId").value(2))
                .andExpect(jsonPath("$[1].title").value("Spring Boot Dev"));

        Mockito.verify(savedJobService).getSavedJobPostings(freelancerId);
    }
}
