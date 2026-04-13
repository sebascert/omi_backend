package com.example.omi.project;

import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ProjectRepository {

  private final JdbcTemplate jdbcTemplate;

  public ProjectRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public List<Map<String, Object>> findAll() {
    return jdbcTemplate.queryForList("SELECT * FROM project");
  }
}
