package com.example.omi.issue;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects/{projectId}/issues")
public class IssueController {

  private final IssueRepository repo;

  public IssueController(IssueRepository repo) {
    this.repo = repo;
  }

  @GetMapping
  public List<IssueDto> list(
      @PathVariable Long projectId, @RequestParam(required = false) Long sprintId) {
    return repo.findByProject(projectId, sprintId);
  }

  @PostMapping
  public void create(@PathVariable Long projectId, @Valid @RequestBody CreateIssueRequest req) {
    repo.create(req);
  }
}
