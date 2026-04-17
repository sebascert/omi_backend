package com.example.omi.sprint;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects/{projectId}/sprints")
public class SprintController {

  private final SprintRepository repo;

  public SprintController(SprintRepository repo) {
    this.repo = repo;
  }

  @GetMapping
  public List<SprintDto> getAll(@PathVariable Long projectId) {
    return repo.findByProject(projectId);
  }

  @PostMapping
  public void create(@PathVariable Long projectId, @Valid @RequestBody CreateSprintRequest req) {
    if (req.getEndDate().isBefore(req.getStartDate())) {
      throw new IllegalArgumentException("endDate must be greater than or equal to startDate");
    }

    repo.create(projectId, req);
  }
}
