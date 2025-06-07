package com.jobmatrix.dto;

import com.jobmatrix.entity.BudgetType;
import com.jobmatrix.entity.ExperienceLevel;
import com.jobmatrix.entity.ProjectDuration;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class JobPostingUpdateRequest {
    private String title;
    private String description;
    private BudgetType budgetType;
    private Integer hourlyMinRate;
    private Integer hourlyMaxRate;
    private Integer fixedPrice;
    private ProjectDuration projectDuration;
    private ExperienceLevel experienceLevel;
    private Long categoryId;
    private List<String> questions;
}
