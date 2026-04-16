package com.example.omi.issue;

import jakarta.validation.constraints.Min;

public class PatchIssueRequest {

  private String title;
  private String description;
  private String type;
  private String status;

  @Min(value = 0, message = "estimatedHours must be >= 0")
  private Integer estimatedHours;

  @Min(value = 0, message = "actualHours must be >= 0")
  private Integer actualHours;

  private Long featureId;
  private Long assigneeId;
  private Boolean isVisible;

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

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Integer getEstimatedHours() {
    return estimatedHours;
  }

  public void setEstimatedHours(Integer estimatedHours) {
    this.estimatedHours = estimatedHours;
  }

  public Integer getActualHours() {
    return actualHours;
  }

  public void setActualHours(Integer actualHours) {
    this.actualHours = actualHours;
  }

  public Long getFeatureId() {
    return featureId;
  }

  public void setFeatureId(Long featureId) {
    this.featureId = featureId;
  }

  public Long getAssigneeId() {
    return assigneeId;
  }

  public void setAssigneeId(Long assigneeId) {
    this.assigneeId = assigneeId;
  }

  public Boolean getIsVisible() {
    return isVisible;
  }

  public void setIsVisible(Boolean isVisible) {
    this.isVisible = isVisible;
  }
}
