package com.jobmatrix.dto;

import com.common.enums.BudgetType;
import com.common.enums.ExperienceLevel;
import com.common.enums.ProjectDuration;
import lombok.*;

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
}
