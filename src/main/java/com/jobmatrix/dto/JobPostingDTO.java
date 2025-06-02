package com.jobmatrix.dto;

import com.jobmatrix.entity.BudgetType;
import com.jobmatrix.entity.ExperienceLevel;
import com.jobmatrix.entity.JobPostingStatus;
import com.jobmatrix.entity.ProjectDuration;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    @NotNull(message = "category_id cannot be null.")
    private Long categoryId;

    private JobPostingStatus jobPostingStatus;
}
