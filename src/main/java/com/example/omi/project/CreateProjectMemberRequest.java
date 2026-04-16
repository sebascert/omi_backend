package com.example.omi.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateProjectMemberRequest {

  @NotNull(message = "userId is required")
  private Long userId;

  @NotBlank(message = "role is required")
  private String role;

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }
}
