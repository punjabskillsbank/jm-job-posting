package com.jobmatrix.dto;

import com.jobmatrix.entity.BudgetType;
import com.jobmatrix.entity.ExperienceLevel;
import com.jobmatrix.entity.ProjectDuration;
import lombok.Data;
import java.util.UUID;

@Data
public class JobPostingDTO {
    private UUID clientId;
    private String title;
    private String description;
    private BudgetType budgetType;
    private Integer hourlyMinRate;
    private Integer hourlyMaxRate;
    private Integer fixedPrice;
    private ProjectDuration projectDuration;
    private ExperienceLevel experienceLevel;
    private Long categoryId;

    // Getters and Setters
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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
} 