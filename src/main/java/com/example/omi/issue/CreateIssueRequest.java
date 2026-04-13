package com.example.omi.issue;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CreateIssueRequest(
    @NotNull Long featureId,
    Long assigneeId,
    @NotBlank String title,
    String description,
    @NotBlank String type,
    @NotBlank String status,
    String priority,
    Integer estimatedHours,
    Integer actualHours,
    LocalDate dueDate,
    Boolean isVisible) {}
