package com.jobmatrix.controller;

import com.jobmatrix.service.FreelancerSavedJobPostingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/freelancer/saved_job_postings")
@Tag(name = "Freelancer Saved Job Postings", description = "APIs for saving and removing job postings for freelancers")
@RequiredArgsConstructor
public class FreelancerSavedJobPostingController {

    private final FreelancerSavedJobPostingService savedJobService;

    @Operation(summary = "Save a job posting to freelancer's saved list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job saved successfully"),
            @ApiResponse(responseCode = "404", description = "JobPosting not found", content = @Content)
    })
    @PostMapping("/{freelancerId}/{jobPostingId}")
    public ResponseEntity<Void> saveJobPosting(@PathVariable UUID freelancerId, @PathVariable Long jobPostingId) {
        savedJobService.saveJobPosting(freelancerId, jobPostingId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Remove a saved job posting")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job removed successfully")
    })
    @DeleteMapping("/{freelancerId}/{jobPostingId}")
    public ResponseEntity<Void> removeJobPosting(@PathVariable UUID freelancerId, @PathVariable Long jobPostingId) {
        savedJobService.removeSavedJobPosting(freelancerId, jobPostingId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get all saved job postings for a freelancer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of saved jobs",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = JobPosting.class))))
    })
    @GetMapping("/{freelancerId}")
    public ResponseEntity<List<JobPosting>> getSavedJobPostings(@PathVariable UUID freelancerId) {
        List<JobPosting> jobPostings = savedJobService.getSavedJobPostings(freelancerId);
        return ResponseEntity.ok(jobPostings);
    }
}
