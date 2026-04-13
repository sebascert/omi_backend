package com.example.omi.project;

import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

  private final ProjectRepository repo;

  public ProjectController(ProjectRepository repo) {
    this.repo = repo;
  }

  @GetMapping
  public List<Map<String, Object>> getAll() {
    return repo.findAll();
  }
}
