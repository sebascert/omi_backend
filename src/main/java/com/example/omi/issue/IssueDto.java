package com.example.omi.issue;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record IssueDto(
    Long id,
    Long projectId,
    Long sprintId,
    Long featureId,
    String title,
    String description,
    String status,
    String type,
    Long assigneeId,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt,
    Integer estimatedHours,
    Integer actualHours,
    Boolean isVisible,
    LocalDate dueDate) {}
