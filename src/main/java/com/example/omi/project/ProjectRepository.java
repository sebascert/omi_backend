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
    return jdbcTemplate.queryForList("SELECT * FROM project ORDER BY id");
  }

  public List<ProjectMemberDto> findMembers(Long projectId) {
    String sql =
        """
        SELECT
            u.id AS user_id,
            u.name,
            r.name AS role_name
        FROM project_member pm
        JOIN users u ON u.id = pm.user_id
        LEFT JOIN role r ON r.id = u.role_id
        WHERE pm.project_id = ?
        ORDER BY u.id
        """;

    return jdbcTemplate.query(
        sql,
        (rs, rowNum) ->
            new ProjectMemberDto(
                rs.getLong("user_id"), rs.getString("name"), rs.getString("role_name")),
        projectId);
  }
}
