package com.example.omi.sprint;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SprintRepository {

  private final JdbcTemplate jdbcTemplate;

  public SprintRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public List<SprintDto> findByProject(Long projectId) {
    String sql =
        """
            SELECT id, name, start_date, end_date, status, project_id
            FROM sprint
            WHERE project_id = ?
            ORDER BY id
        """;

    return jdbcTemplate.query(
        sql,
        (rs, rowNum) ->
            new SprintDto(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getDate("start_date").toLocalDate(),
                rs.getDate("end_date").toLocalDate(),
                rs.getString("status"),
                rs.getLong("project_id")),
        projectId);
  }

  public void create(Long projectId, CreateSprintRequest req) {
    String sql =
        """
        INSERT INTO sprint (
            id,
            name,
            start_date,
            end_date,
            goal,
            status,
            project_id
        ) VALUES (
            (SELECT COALESCE(MAX(id), 0) + 1 FROM sprint),
            ?, ?, ?, ?, ?, ?
        )
        """;

    jdbcTemplate.update(
        sql,
        req.getName(),
        java.sql.Date.valueOf(req.getStartDate()),
        java.sql.Date.valueOf(req.getEndDate()),
        req.getGoal(),
        req.getStatus(),
        projectId);
  }

  public void delete(Long projectId, Long sprintId) {
    Integer count =
        jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM sprint WHERE id = ? AND project_id = ?",
            Integer.class,
            sprintId,
            projectId);

    if (count == null || count == 0) {
      throw new org.springframework.dao.EmptyResultDataAccessException(1);
    }

    jdbcTemplate.update(
        """
        DELETE FROM timelog
        WHERE issue_id IN (
            SELECT i.id
            FROM issues i
            JOIN feature f ON f.id = i.feature_id
            WHERE f.sprint_id = ?
        )
        """,
        sprintId);

    jdbcTemplate.update(
        """
        DELETE FROM issue_log
        WHERE issue_id IN (
            SELECT i.id
            FROM issues i
            JOIN feature f ON f.id = i.feature_id
            WHERE f.sprint_id = ?
        )
        """,
        sprintId);

    jdbcTemplate.update(
        """
        DELETE FROM issues
        WHERE feature_id IN (
            SELECT id FROM feature WHERE sprint_id = ?
        )
        """,
        sprintId);

    jdbcTemplate.update("DELETE FROM feature WHERE sprint_id = ?", sprintId);

    int rows =
        jdbcTemplate.update(
            "DELETE FROM sprint WHERE id = ? AND project_id = ?", sprintId, projectId);

    if (rows == 0) {
      throw new org.springframework.dao.EmptyResultDataAccessException(1);
    }
  }
}
