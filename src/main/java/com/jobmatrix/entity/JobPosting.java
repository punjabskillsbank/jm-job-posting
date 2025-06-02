package com.jobmatrix.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "job_postings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class JobPosting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_posting_id")
    private Long jobPostingId;

    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "budget_type", columnDefinition = "budget_type")
    private BudgetType budgetType;

    @Column(name = "hourly_min_rate")
    private Integer hourlyMinRate;

    @Column(name = "hourly_max_rate")
    private Integer hourlyMaxRate;

    @Column(name = "fixed_price")
    private Integer fixedPrice;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "project_duration", columnDefinition = "project_duration")
    private ProjectDuration projectDuration;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "experience_level", columnDefinition = "experience_level")
    private ExperienceLevel experienceLevel;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "job_posting_status", columnDefinition = "job_posting_status")
    private JobPostingStatus jobPostingStatus;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToMany
    @JoinTable(
            name = "job_postings_skills",
            joinColumns = @JoinColumn(name = "job_posting_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    @JsonManagedReference
    private Set<Skill> skills = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JobPosting)) return false;
        JobPosting other = (JobPosting) o;
        return jobPostingId != null && jobPostingId.equals(other.getJobPostingId());
    }

    @Override
    public int hashCode() {
        return jobPostingId != null ? jobPostingId.hashCode() : 0;
    }

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
