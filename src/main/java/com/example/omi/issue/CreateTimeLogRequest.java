package com.example.omi.issue;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class CreateTimeLogRequest {

  @NotNull(message = "userId is required")
  private Long userId;

  @NotNull(message = "hoursLogged is required")
  @DecimalMin(value = "0.0", inclusive = false, message = "hoursLogged must be > 0")
  private BigDecimal hoursLogged;

  @NotNull(message = "logDate is required")
  private LocalDate logDate;

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public BigDecimal getHoursLogged() {
    return hoursLogged;
  }

  public void setHoursLogged(BigDecimal hoursLogged) {
    this.hoursLogged = hoursLogged;
  }

  public LocalDate getLogDate() {
    return logDate;
  }

  public void setLogDate(LocalDate logDate) {
    this.logDate = logDate;
  }
}
