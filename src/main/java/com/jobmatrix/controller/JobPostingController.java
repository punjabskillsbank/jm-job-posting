package com.jobmatrix.controller;

import com.jobmatrix.dto.JobPostingDTO;
import com.jobmatrix.entity.JobPosting;
import com.jobmatrix.service.JobPostingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/job_postings")
@Tag(name = "Job Posting ", description = "Operations related to job posting ")
public class JobPostingController {

    @Autowired
    private JobPostingService jobPostingService;

    @PostMapping("/create_job_posting")
    public ResponseEntity<JobPosting> createJobPosting(@RequestBody JobPostingDTO jobPostingDTO) {
        JobPosting createdJobPosting = jobPostingService.createJobPosting(jobPostingDTO);
        return new ResponseEntity<>(createdJobPosting, HttpStatus.CREATED);
    }

    @GetMapping("/open_job_postings")
    public ResponseEntity<List<JobPosting>> getOpenJobPostings() {
        List<JobPosting> openJobPostings = jobPostingService.getOpenJobPostings();
        return ResponseEntity.ok(openJobPostings);
    }
} 