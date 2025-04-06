package com.jobmatrix.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jobmatrix.entity.BudgetType;
import com.jobmatrix.entity.ExperienceLevel;
import com.jobmatrix.entity.ProjectDuration;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class JobPostingDTO {

    @NotNull(message = "client_id cannot be null.")
    @JsonProperty("client_id")
    @Column(name = "client_id")
    private UUID clientId;

    @NotBlank(message = "title cannot be blank.")
    @JsonProperty("title")
    @Column(name = "title")
    private String title;

    @NotBlank(message = "description cannot be blank.")
    @JsonProperty("description")
    @Column(name = "description")
    private String description;

    @NotNull(message = "budget_type cannot be null.")
    @JsonProperty("budget_type")
    @Column(name = "budget_type")
    private BudgetType budgetType;

    @JsonProperty("hourly_min_rate")
    @Column(name = "hourly_min_rate")
    private Integer hourlyMinRate;

    @JsonProperty("hourly_max_rate")
    @Column(name = "hourly_max_rate")
    private Integer hourlyMaxRate;

    @JsonProperty("fixed_price")
    @Column(name = "fixed_price")
    private Integer fixedPrice;

    @NotNull(message = "project_duration cannot be null.")
    @JsonProperty("project_duration")
    @Column(name = "project_duration")
    private ProjectDuration projectDuration;

    @NotNull(message = "experience_level cannot be null.")
    @JsonProperty("experience_level")
    @Column(name = "experience_level")
    private ExperienceLevel experienceLevel;

    @NotNull(message = "category_id cannot be null.")
    @JsonProperty("category_id")
    @Column(name = "category_id")
    private Long categoryId;
}
