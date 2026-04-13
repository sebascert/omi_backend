package com.example.omi.sprint;

import java.time.LocalDate;

public record SprintDto(
    Long id, String name, LocalDate startDate, LocalDate endDate, String status, Long projectId) {}
