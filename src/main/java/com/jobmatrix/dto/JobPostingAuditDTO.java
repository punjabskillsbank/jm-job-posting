package com.jobmatrix.dto;

import com.common.dto.CategoryDTO;
import com.common.dto.JobPostingQuestionDTO;
import com.common.dto.SkillDTO;
import com.common.enums.BudgetType;
import com.common.enums.ExperienceLevel;
import com.common.enums.JobPostingStatus;
import com.common.enums.ProjectDuration;
import lombok.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobPostingAuditDTO {

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
