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
} 