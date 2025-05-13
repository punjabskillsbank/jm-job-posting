package com.jobmatrix.controller;

import com.jobmatrix.dto.JobPostingDTO;
import com.jobmatrix.dto.JobPostingUpdateRequest;
import com.jobmatrix.entity.Category;
import com.jobmatrix.entity.JobPosting;
import com.jobmatrix.entity.JobPostingStatus;
import com.jobmatrix.service.JobPostingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @GetMapping("/{jobPostingId}")
    public ResponseEntity<JobPosting> getJobPostingById(@PathVariable Long jobPostingId){
        JobPosting jobPosting = jobPostingService.getJobPostingById(jobPostingId);
        return ResponseEntity.ok(jobPosting);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<JobPosting>> getJobPostingsByClientId(@PathVariable UUID clientId) {
        List<JobPosting> jobPostings = jobPostingService.getJobPostingsByClientId(clientId);
        return ResponseEntity.ok(jobPostings);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getCategories() {
        List<Category> categories = jobPostingService.getCategories();
        return ResponseEntity.ok(categories);
    }

    @PatchMapping("/{job_posting_id}")
    public ResponseEntity<JobPosting> updateJobPosting(
            @PathVariable Long job_posting_id,
            @Valid @RequestBody JobPostingUpdateRequest jobPostingUpdateRequest
    ){
        JobPosting updatedJobPosting = jobPostingService.updateJobPosting(job_posting_id, jobPostingUpdateRequest);
        return ResponseEntity.ok(updatedJobPosting);
    }

    @GetMapping("/{clientId}/statuses/{statuses}")
    public ResponseEntity<Map<JobPostingStatus, List<JobPostingDTO>>> getJobPostingsByStatuses(
            @PathVariable UUID clientId,
            @PathVariable String statuses) {
        // Convert comma separated string to list of JobPostingStatus enum values
        List<JobPostingStatus> statusList = Arrays.stream(statuses.split(","))
                .map(String::toUpperCase)
                .map(JobPostingStatus::valueOf)
                .collect(Collectors.toList());
        Map<JobPostingStatus, List<JobPostingDTO>> result = jobPostingService.getJobPostingsByStatuses(clientId, statusList);
        return ResponseEntity.ok(result);
    }
} 