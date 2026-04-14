package com.example.omi.issue;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class IssueRepository {

  private final JdbcTemplate jdbc;

  public IssueRepository(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  public List<IssueDto> findByProject(Long projectId, Long sprintId) {
    String sql =
        """
        SELECT
            i.id,
            s.project_id,
            f.sprint_id,
            i.feature_id,
            i.title,
            i.description,
            i.status,
            i.type,
            i.assigned_to,
            i.created_at,
            i.updated_at,
            i.estimated_hours,
            i.actual_hours,
            i.is_visible
        FROM issues i
        JOIN feature f ON f.id = i.feature_id
        JOIN sprint s ON s.id = f.sprint_id
        WHERE s.project_id = ?
          AND (? IS NULL OR f.sprint_id = ?)
        ORDER BY i.id
        """;

    return jdbc.query(sql, this::mapIssueDto, projectId, sprintId, sprintId);
  }

  private IssueDto mapIssueDto(ResultSet rs, int rowNum) throws SQLException {
    return new IssueDto(
        rs.getLong("id"),
        rs.getLong("project_id"),
        rs.getLong("sprint_id"),
        rs.getLong("feature_id"),
        rs.getString("title"),
        rs.getString("description"),
        rs.getString("status"),
        rs.getString("type"),
        rs.getLong("assigned_to"),
        rs.getObject("created_at", OffsetDateTime.class),
        rs.getObject("updated_at", OffsetDateTime.class),
        rs.getObject("estimated_hours", Integer.class),
        rs.getObject("actual_hours", Integer.class),
        rs.getInt("is_visible") == 1);
  }

  public void create(CreateIssueRequest r) {
    String sql =
        """
        INSERT INTO issues (
            id,
            title,
            description,
            type,
            status,
            estimated_hours,
            actual_hours,
            feature_id,
            assigned_to,
            is_visible,
            created_at,
            updated_at
        ) VALUES (
            (
              SELECT COALESCE(MAX(id), 0) + 1
              FROM issues
            ),
            ?, ?, ?, ?,
            ?, ?, ?, ?,
            ?, SYSTIMESTAMP, NULL
        )
        """;

    jdbc.update(
        sql,
        r.getTitle(),
        r.getDescription(),
        r.getType(),
        r.getStatus(),
        r.getEstimatedHours(),
        r.getActualHours(),
        r.getFeatureId(),
        r.getAssigneeId(),
        r.getIsVisible() ? 1 : 0);
  }
}
