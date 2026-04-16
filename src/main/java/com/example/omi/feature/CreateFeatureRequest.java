package com.example.omi.feature;

import jakarta.validation.constraints.NotBlank;

public class CreateFeatureRequest {

  @NotBlank(message = "title is required")
  private String title;

  private String description;

  @NotBlank(message = "priority is required")
  private String priority;

  @NotBlank(message = "status is required")
  private String status;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getPriority() {
    return priority;
  }

  public void setPriority(String priority) {
    this.priority = priority;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
