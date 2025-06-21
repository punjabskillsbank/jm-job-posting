package com.jobmatrix.controller;

import com.jobmatrix.entity.JobPosting;
import com.jobmatrix.service.FreelancerSavedJobService;
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
@RequestMapping("/api/freelancer/saved-jobs")
@Tag(name = "Freelancer Saved Jobs", description = "APIs for saving and removing job postings for freelancers")
@RequiredArgsConstructor
public class FreelancerSavedJobController {

    private final FreelancerSavedJobService savedJobService;

    @Operation(summary = "Save a job posting to freelancer's saved list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job saved successfully"),
            @ApiResponse(responseCode = "404", description = "JobPosting not found", content = @Content)
    })
    @PostMapping("/{freelancerId}/{jobPostingId}")
    public ResponseEntity<Void> saveJob(@PathVariable UUID freelancerId, @PathVariable Long jobPostingId) {
        savedJobService.saveJob(freelancerId, jobPostingId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Remove a saved job posting")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job removed successfully")
    })
    @DeleteMapping("/{freelancerId}/{jobPostingId}")
    public ResponseEntity<Void> removeJob(@PathVariable UUID freelancerId, @PathVariable Long jobPostingId) {
        savedJobService.removeSavedJob(freelancerId, jobPostingId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get all saved job postings for a freelancer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of saved jobs",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = JobPosting.class))))
    })
    @GetMapping("/{freelancerId}")
    public ResponseEntity<List<JobPosting>> getSavedJobs(@PathVariable UUID freelancerId) {
        List<JobPosting> jobs = savedJobService.getSavedJobs(freelancerId);
        return ResponseEntity.ok(jobs);
    }
}
