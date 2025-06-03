package com.jobmatrix.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.jobmatrix.entity.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
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

    @NotNull(message = "category_id cannot be null.")
    private CategoryDTO category;
    private Long categoryId;

    @NotNull(message = "At least one skill ID must be provided.")
    private Set<SkillDTO> skills;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<Long> skillIds;

    private JobPostingStatus jobPostingStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
