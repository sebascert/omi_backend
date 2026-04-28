package com.example.omi.overdue;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateOverdueReportRequest {

  @NotNull(message = "issueId is required")
  private Long issueId;

  @NotBlank(message = "title is required")
  private String title;

  private String notes;

  public Long getIssueId() {
    return issueId;
  }

  public void setIssueId(Long issueId) {
    this.issueId = issueId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }
}
