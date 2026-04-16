package com.example.omi.kpi;

import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects/{projectId}/kpis")
public class KpiController {

  private final KpiRepository repo;

  public KpiController(KpiRepository repo) {
    this.repo = repo;
  }

  @GetMapping("/summary")
  public KpiSummaryDto summary(
      @PathVariable Long projectId, @RequestParam(required = false) Long sprintId) {
    return repo.getSummary(projectId, sprintId);
  }

  @GetMapping("/tasks-by-user")
  public List<TasksByUserDto> tasksByUser(
      @PathVariable Long projectId, @RequestParam(required = false) Long sprintId) {
    return repo.getTasksByUser(projectId, sprintId);
  }

  @GetMapping("/hours-by-user")
  public List<HoursByUserDto> hoursByUser(
      @PathVariable Long projectId, @RequestParam(required = false) Long sprintId) {
    return repo.getHoursByUser(projectId, sprintId);
  }
}
