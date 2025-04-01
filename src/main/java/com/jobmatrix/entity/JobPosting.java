package com.jobmatrix.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "job_postings")
@Data
public class JobPosting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_posting_id")
    private Long jobPostingId;

    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    @Column(name = "title")
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "budget_type")
    private BudgetType budgetType;

    @Column(name = "hourly_min_rate")
    private Integer hourlyMinRate;

    @Column(name = "hourly_max_rate")
    private Integer hourlyMaxRate;

    @Column(name = "fixed_price")
    private Integer fixedPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "project_duration")
    private ProjectDuration projectDuration;

    @Enumerated(EnumType.STRING)
    @Column(name = "experience_level")
    private ExperienceLevel experienceLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_posting_status")
    private JobPostingStatus jobPostingStatus;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Getters and Setters
    public Long getJobPostingId() {
        return jobPostingId;
    }

    public void setJobPostingId(Long jobPostingId) {
        this.jobPostingId = jobPostingId;
    }

    public UUID getClientId() {
        return clientId;
    }

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BudgetType getBudgetType() {
        return budgetType;
    }

    public void setBudgetType(BudgetType budgetType) {
        this.budgetType = budgetType;
    }

    public Integer getHourlyMinRate() {
        return hourlyMinRate;
    }

    public void setHourlyMinRate(Integer hourlyMinRate) {
        this.hourlyMinRate = hourlyMinRate;
    }

    public Integer getHourlyMaxRate() {
        return hourlyMaxRate;
    }

    public void setHourlyMaxRate(Integer hourlyMaxRate) {
        this.hourlyMaxRate = hourlyMaxRate;
    }

    public Integer getFixedPrice() {
        return fixedPrice;
    }

    public void setFixedPrice(Integer fixedPrice) {
        this.fixedPrice = fixedPrice;
    }

    public ProjectDuration getProjectDuration() {
        return projectDuration;
    }

    public void setProjectDuration(ProjectDuration projectDuration) {
        this.projectDuration = projectDuration;
    }

    public ExperienceLevel getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(ExperienceLevel experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    public JobPostingStatus getJobPostingStatus() {
        return jobPostingStatus;
    }

    public void setJobPostingStatus(JobPostingStatus jobPostingStatus) {
        this.jobPostingStatus = jobPostingStatus;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
} 