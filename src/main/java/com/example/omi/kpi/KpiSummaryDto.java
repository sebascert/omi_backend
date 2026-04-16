package com.example.omi.kpi;

import java.math.BigDecimal;

public record KpiSummaryDto(
    Integer totalTasks,
    BigDecimal totalActualHours,
    BigDecimal avgTasksPerDev,
    BigDecimal avgHoursPerDev) {}
