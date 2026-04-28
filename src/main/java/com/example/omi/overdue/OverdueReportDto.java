package com.example.omi.overdue;

import java.time.OffsetDateTime;

public record OverdueReportDto(
    Long id, Long issueId, OffsetDateTime generatedAt, String title, String notes) {}
