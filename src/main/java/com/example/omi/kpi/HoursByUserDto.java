package com.example.omi.kpi;

import java.math.BigDecimal;

public record HoursByUserDto(Long userId, String user, BigDecimal hours) {}
