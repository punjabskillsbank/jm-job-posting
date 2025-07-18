package com.jobmatrix.controller;

import com.common.dto.JobPostingDTO;
import com.common.entity.JobPosting;
import com.common.enums.JobPostingStatus;
import com.common.util.EnumUtils;
import com.jobmatrix.dto.JobPostingUpdateRequest;
import com.jobmatrix.service.JobPostingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/job_postings")
@Tag(name = "Job Posting ", description = "Operations related to job posting ")
public class JobPostingController {

    @Autowired
    private JobPostingService jobPostingService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/create_job_posting")
    public ResponseEntity<JobPostingDTO> createJobPosting(@RequestBody JobPostingDTO jobPostingDTO) {
        JobPostingDTO createdJobPostingDTO = jobPostingService.createJobPosting(jobPostingDTO);
        return new ResponseEntity<>(createdJobPostingDTO, HttpStatus.CREATED);
    }

    @GetMapping("/open_job_postings")
    public ResponseEntity<List<JobPostingDTO>> getOpenJobPostings() {
        List<JobPostingDTO> openJobPostings = jobPostingService.getOpenJobPostings();
        return ResponseEntity.ok(openJobPostings);
    }


    @GetMapping("/{jobPostingId}")
    public ResponseEntity<JobPostingDTO> getJobPostingById(@PathVariable Long jobPostingId) {
        JobPosting jobPosting = jobPostingService.getJobPostingById(jobPostingId);

        JobPostingDTO jobPostingDTO = modelMapper.map(jobPosting, JobPostingDTO.class);
        return ResponseEntity.ok(jobPostingDTO);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<JobPosting>> getJobPostingsByClientId(@PathVariable UUID clientId) {
        List<JobPosting> jobPostings = jobPostingService.getJobPostingsByClientId(clientId);
        return ResponseEntity.ok(jobPostings);
    }

    @GetMapping("/categories")
    public ResponseEntity<Map<String, List<String>>> getCategories() {
        Map<String, List<String>> groupedCategories = jobPostingService.getCategories();
        return ResponseEntity.ok(groupedCategories);
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

        List<JobPostingStatus> statusList = EnumUtils.parseEnumList(statuses, JobPostingStatus.class);
        Map<JobPostingStatus, List<JobPostingDTO>> result = jobPostingService.getJobPostingsByStatuses(clientId, statusList);
        return ResponseEntity.ok(result);
    }

} 