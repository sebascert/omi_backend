package com.example.omi.feature;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sprints/{sprintId}/features")
public class FeatureController {

  private final FeatureRepository repo;

  public FeatureController(FeatureRepository repo) {
    this.repo = repo;
  }

  @PostMapping
  public void create(@PathVariable Long sprintId, @Valid @RequestBody CreateFeatureRequest req) {
    repo.create(sprintId, req);
  }

  @GetMapping
  public List<FeatureDto> list(@PathVariable Long sprintId) {
    return repo.findBySprint(sprintId);
  }

  @DeleteMapping("/{featureId}")
  public ResponseEntity<Void> delete(@PathVariable Long sprintId, @PathVariable Long featureId) {

    repo.delete(sprintId, featureId);
    return ResponseEntity.noContent().build();
  }
}
