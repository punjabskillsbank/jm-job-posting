package com.jobmatrix.dto;

import com.common.dto.CategoryDTO;
import com.common.dto.SkillDTO;
import com.common.enums.BudgetType;
import com.common.enums.ExperienceLevel;
import com.common.enums.JobPostingStatus;
import com.common.enums.ProjectDuration;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder(toBuilder = true)
public class JobPostingDTO {

    private Long jobPostingId;

    @NotNull(message = "client_id cannot be null.")
    private UUID clientId;

    @NotBlank(message = "title cannot be blank.")
    private String title;

    @NotBlank(message = "description cannot be blank.")
    private String description;

    @NotNull(message = "budget_type cannot be null.")
    private BudgetType budgetType;

    private Integer hourlyMinRate;

    private Integer hourlyMaxRate;

    private Integer fixedPrice;

    @NotNull(message = "project_duration cannot be null.")
    private ProjectDuration projectDuration;

    @NotNull(message = "experience_level cannot be null.")
    private ExperienceLevel experienceLevel;

    @NotNull(message = "category  cannot be null.")
    private CategoryDTO category;

    @NotNull(message = "At least one skill must be provided.")
    private Set<SkillDTO> skills;

    private JobPostingStatus jobPostingStatus;

    private List<JobPostingQuestionDTO> questions;
}