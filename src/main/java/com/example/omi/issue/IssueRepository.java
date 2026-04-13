package com.example.omi.issue;

import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class IssueRepository {

  private final JdbcTemplate jdbcTemplate;

  public IssueRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  private final RowMapper<IssueDto> mapper =
      (rs, rowNum) -> {
        Timestamp created = rs.getTimestamp("created_at");
        Timestamp updated = rs.getTimestamp("updated_at");

        return new IssueDto(
            rs.getLong("id"),
            rs.getLong("project_id"),
            rs.getLong("sprint_id"),
            rs.getLong("feature_id"),
            rs.getString("title"),
            rs.getString("description"),
            rs.getString("status"),
            rs.getString("priority"),
            rs.getString("type"),
            rs.getObject("assigned_to") == null ? null : rs.getLong("assigned_to"),
            rs.getDate("due_date") == null ? null : rs.getDate("due_date").toLocalDate(),
            created == null ? null : created.toInstant().atOffset(ZoneOffset.UTC),
            updated == null ? null : updated.toInstant().atOffset(ZoneOffset.UTC),
            rs.getObject("estimated_hours") == null ? null : rs.getInt("estimated_hours"),
            rs.getObject("actual_hours") == null ? null : rs.getInt("actual_hours"),
            rs.getInt("is_visible") == 1);
      };

  public List<IssueDto> findByProject(Long projectId, Long sprintId) {
    String sql =
        """
            SELECT i.id, p.id project_id, s.id sprint_id, f.id feature_id,
                   i.title, i.description, i.status, i.priority, i.type,
                   i.assigned_to, i.due_date, i.created_at, i.updated_at,
                   i.estimated_hours, i.actual_hours, i.is_visible
            FROM issues i
            JOIN feature f ON f.id = i.feature_id
            JOIN sprint s ON s.id = f.sprint_id
            JOIN project p ON p.id = s.project_id
            WHERE p.id = ?
              AND (? IS NULL OR s.id = ?)
        """;

    return jdbcTemplate.query(sql, mapper, projectId, sprintId, sprintId);
  }

  public void create(CreateIssueRequest r) {
    String sql =
        """
            INSERT INTO issues (
                id, title, description, type, status, priority,
                estimated_hours, actual_hours, feature_id, assigned_to,
                due_date, is_visible, created_at, updated_at
            ) VALUES (
                seq_issue.NEXTVAL, ?, ?, ?, ?, ?,
                ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
            )
        """;

    jdbcTemplate.update(
        sql,
        r.title(),
        r.description(),
        r.type(),
        r.status(),
        r.priority(),
        r.estimatedHours(),
        r.actualHours(),
        r.featureId(),
        r.assigneeId(),
        r.dueDate(),
        r.isVisible() == null ? 1 : (r.isVisible() ? 1 : 0));
  }
}
