package com.jobmatrix.dto;

import com.common.enums.BudgetType;
import com.common.enums.ExperienceLevel;
import com.common.enums.JobPostingStatus;
import com.common.enums.ProjectDuration;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class JobPostingUpdateRequest {
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
    private JobPostingStatus jobPostingStatus;
}
