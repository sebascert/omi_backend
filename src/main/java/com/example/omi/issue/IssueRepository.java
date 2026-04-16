package com.example.omi.issue;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class IssueRepository {

  private final JdbcTemplate jdbc;

  public IssueRepository(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  public List<IssueDto> findByProject(
      Long projectId,
      Long sprintId,
      String status,
      Long assignedTo,
      LocalDate startDate,
      LocalDate endDate) {

    String normalizedStatus = normalizeIssueStatus(status);

    StringBuilder sql =
        new StringBuilder(
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
            """);

    List<Object> args = new ArrayList<>();
    args.add(projectId);

    if (sprintId != null) {
      sql.append(" AND f.sprint_id = ?");
      args.add(sprintId);
    }

    if (normalizedStatus != null) {
      sql.append(" AND i.status = ?");
      args.add(normalizedStatus);
    }

    if (assignedTo != null) {
      sql.append(" AND i.assigned_to = ?");
      args.add(assignedTo);
    }

    if (startDate != null) {
      sql.append(" AND TRUNC(i.created_at) >= ?");
      args.add(java.sql.Date.valueOf(startDate));
    }

    if (endDate != null) {
      sql.append(" AND TRUNC(i.created_at) <= ?");
      args.add(java.sql.Date.valueOf(endDate));
    }

    sql.append(" ORDER BY i.id");

    return jdbc.query(sql.toString(), this::mapIssueDto, args.toArray());
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
            (SELECT COALESCE(MAX(id), 0) + 1 FROM issues),
            ?, ?, ?, ?, ?, ?, ?, ?, ?, SYSTIMESTAMP, NULL
        )
        """;

    jdbc.update(
        sql,
        r.getTitle(),
        r.getDescription(),
        normalizeIssueType(r.getType()),
        normalizeIssueStatus(r.getStatus()),
        r.getEstimatedHours(),
        r.getActualHours(),
        r.getFeatureId(),
        r.getAssigneeId(),
        Boolean.TRUE.equals(r.getIsVisible()) ? 1 : 0);
  }

  public void patch(Long issueId, PatchIssueRequest r) {
    String sql =
        """
        UPDATE issues
        SET
            title = COALESCE(?, title),
            description = COALESCE(?, description),
            type = COALESCE(?, type),
            status = COALESCE(?, status),
            estimated_hours = COALESCE(?, estimated_hours),
            actual_hours = COALESCE(?, actual_hours),
            feature_id = COALESCE(?, feature_id),
            assigned_to = COALESCE(?, assigned_to),
            is_visible = COALESCE(?, is_visible),
            updated_at = SYSTIMESTAMP
        WHERE id = ?
        """;

    Integer visibleValue = null;
    if (r.getIsVisible() != null) {
      visibleValue = r.getIsVisible() ? 1 : 0;
    }

    jdbc.update(
        sql,
        r.getTitle(),
        r.getDescription(),
        normalizeIssueType(r.getType()),
        normalizeIssueStatus(r.getStatus()),
        r.getEstimatedHours(),
        r.getActualHours(),
        r.getFeatureId(),
        r.getAssigneeId(),
        visibleValue,
        issueId);
  }

  public List<TimeLogDto> findTimeLogsByIssue(Long issueId) {
    String sql =
        """
        SELECT
            t.id,
            t.issue_id,
            t.user_id,
            u.name AS user_name,
            t.hours_logged,
            t.log_date,
            f.sprint_id,
            s.project_id
        FROM timelog t
        JOIN issues i ON i.id = t.issue_id
        JOIN users u ON u.id = t.user_id
        JOIN feature f ON f.id = i.feature_id
        JOIN sprint s ON s.id = f.sprint_id
        WHERE t.issue_id = ?
        ORDER BY t.id
        """;

    return jdbc.query(sql, this::mapTimeLogDto, issueId);
  }

  public void createTimeLog(Long issueId, CreateTimeLogRequest r) {
    String sql =
        """
        INSERT INTO timelog (
            id,
            issue_id,
            user_id,
            hours_logged,
            log_date
        ) VALUES (
            (SELECT COALESCE(MAX(id), 0) + 1 FROM timelog),
            ?, ?, ?, ?
        )
        """;

    jdbc.update(
        sql, issueId, r.getUserId(), r.getHoursLogged(), java.sql.Date.valueOf(r.getLogDate()));
  }

  public List<TimeLogDto> findTimeLogsByProject(Long projectId, Long sprintId) {
    StringBuilder sql =
        new StringBuilder(
            """
            SELECT
                t.id,
                t.issue_id,
                t.user_id,
                u.name AS user_name,
                t.hours_logged,
                t.log_date,
                f.sprint_id,
                s.project_id
            FROM timelog t
            JOIN issues i ON i.id = t.issue_id
            JOIN users u ON u.id = t.user_id
            JOIN feature f ON f.id = i.feature_id
            JOIN sprint s ON s.id = f.sprint_id
            WHERE s.project_id = ?
            """);

    List<Object> args = new ArrayList<>();
    args.add(projectId);

    if (sprintId != null) {
      sql.append(" AND f.sprint_id = ?");
      args.add(sprintId);
    }

    sql.append(" ORDER BY t.id");

    return jdbc.query(sql.toString(), this::mapTimeLogDto, args.toArray());
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

  private TimeLogDto mapTimeLogDto(ResultSet rs, int rowNum) throws SQLException {
    return new TimeLogDto(
        rs.getLong("id"),
        rs.getLong("issue_id"),
        rs.getLong("user_id"),
        rs.getString("user_name"),
        rs.getBigDecimal("hours_logged"),
        rs.getDate("log_date").toLocalDate(),
        rs.getLong("sprint_id"),
        rs.getLong("project_id"));
  }

  private String normalizeIssueStatus(String status) {
    if (status == null || status.isBlank()) {
      return null;
    }

    return switch (status.trim().toUpperCase()) {
      case "OPEN", "TODO", "TO_DO" -> "open";
      case "IN_PROGRESS", "INPROGRESS" -> "in_progress";
      case "DONE", "CLOSED" -> "closed";
      default -> status;
    };
  }

  private String normalizeIssueType(String type) {
    if (type == null || type.isBlank()) {
      return null;
    }
    return type.trim().toUpperCase();
  }
}
