package com.example.omi.issue;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TimeLogDto(
    Long id,
    Long issueId,
    Long userId,
    String userName,
    BigDecimal hoursLogged,
    LocalDate logDate,
    Long sprintId,
    Long projectId) {}
