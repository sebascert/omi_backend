package com.example.omi.overdue;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/overdue-reports")
public class OverdueReportController {

  private final OverdueReportRepository repo;

  public OverdueReportController(OverdueReportRepository repo) {
    this.repo = repo;
  }

  @GetMapping
  public List<OverdueReportDto> getAll() {
    return repo.findAll();
  }

  @GetMapping("/{reportId}")
  public OverdueReportDto getById(@PathVariable Long reportId) {
    return repo.findById(reportId);
  }

  @PostMapping
  public void create(@Valid @RequestBody CreateOverdueReportRequest req) {
    if (!repo.issueExists(req.getIssueId())) {
      throw new IllegalArgumentException("Issue " + req.getIssueId() + " does not exist");
    }

    repo.create(req);
  }

  @PutMapping("/{reportId}")
  public void update(
      @PathVariable Long reportId, @Valid @RequestBody UpdateOverdueReportRequest req) {

    if (!repo.issueExists(req.getIssueId())) {
      throw new IllegalArgumentException("Issue " + req.getIssueId() + " does not exist");
    }

    repo.update(reportId, req);
  }

  @DeleteMapping("/{reportId}")
  public ResponseEntity<Void> delete(@PathVariable Long reportId) {
    repo.delete(reportId);
    return ResponseEntity.noContent().build();
  }
}
