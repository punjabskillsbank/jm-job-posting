package com.jobmatrix.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "freelancer_saved_jobs")
public class FreelancerSavedJobPosting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "freelancer_saved_jobs_id")
    private Long id;

    @Column(name = "freelancer_id", nullable = false)
    private UUID freelancerId;

    @Column(name = "job_posting_id", nullable = false)
    private Long jobPostingId;

}

