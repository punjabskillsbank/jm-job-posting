package com.jobmatrix.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.*;
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
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
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
    private Set<Skill> skills = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
