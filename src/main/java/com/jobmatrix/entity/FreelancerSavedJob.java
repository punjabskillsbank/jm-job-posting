package com.jobmatrix.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "freelancer_saved_jobs")
public class FreelancerSavedJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "freelancer_saved_jobs_id")
    private Long id;

    @Column(name = "freelancer_id", nullable = false)
    private UUID freelancerId;

    @Column(name = "job_posting_id", nullable = false)
    private Long jobPostingId;

    // Constructors
    public FreelancerSavedJob() {}

    public FreelancerSavedJob(UUID freelancerId, Long jobPostingId) {
        this.freelancerId = freelancerId;
        this.jobPostingId = jobPostingId;
    }

}

