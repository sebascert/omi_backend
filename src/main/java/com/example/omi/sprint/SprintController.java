package com.example.omi.sprint;

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
}
