package com.example.omi.kpi;

import java.util.ArrayList;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class KpiRepository {

  private final JdbcTemplate jdbc;

  public KpiRepository(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  public KpiSummaryDto getSummary(Long projectId, Long sprintId) {
    String sprintFilter = sprintId != null ? " AND f.sprint_id = ? " : "";
    List<Object> args = new ArrayList<>();
    args.add(projectId);
    if (sprintId != null) {
      args.add(sprintId);
    }

    String sql =
        """
        SELECT
            COUNT(i.id) AS total_tasks,
            COALESCE(SUM(i.actual_hours), 0) AS total_actual_hours,
            CASE
                WHEN COUNT(DISTINCT i.assigned_to) = 0 THEN 0
                ELSE ROUND(COUNT(i.id) / COUNT(DISTINCT i.assigned_to), 2)
            END AS avg_tasks_per_dev,
            CASE
                WHEN COUNT(DISTINCT i.assigned_to) = 0 THEN 0
                ELSE ROUND(COALESCE(SUM(i.actual_hours), 0) / COUNT(DISTINCT i.assigned_to), 2)
            END AS avg_hours_per_dev
        FROM issues i
        JOIN feature f ON f.id = i.feature_id
        JOIN sprint s ON s.id = f.sprint_id
        WHERE s.project_id = ?
        """
            + sprintFilter;

    try {
      return jdbc.queryForObject(
          sql,
          (rs, rowNum) ->
              new KpiSummaryDto(
                  rs.getInt("total_tasks"),
                  rs.getBigDecimal("total_actual_hours"),
                  rs.getBigDecimal("avg_tasks_per_dev"),
                  rs.getBigDecimal("avg_hours_per_dev")),
          args.toArray());
    } catch (org.springframework.dao.EmptyResultDataAccessException e) {
      return new KpiSummaryDto(
          0, java.math.BigDecimal.ZERO, java.math.BigDecimal.ZERO, java.math.BigDecimal.ZERO);
    }
  }

  public List<TasksByUserDto> getTasksByUser(Long projectId, Long sprintId) {
    StringBuilder sql =
        new StringBuilder(
            """
            SELECT
                u.name AS user_name,
                COUNT(i.id) AS tasks_completed
            FROM issues i
            JOIN users u ON u.id = i.assigned_to
            JOIN feature f ON f.id = i.feature_id
            JOIN sprint s ON s.id = f.sprint_id
            WHERE s.project_id = ?
              AND i.status = 'closed'
            """);

    List<Object> args = new ArrayList<>();
    args.add(projectId);

    if (sprintId != null) {
      sql.append(" AND f.sprint_id = ?");
      args.add(sprintId);
    }

    sql.append(" GROUP BY u.name ORDER BY u.name");

    return jdbc.query(
        sql.toString(),
        (rs, rowNum) -> new TasksByUserDto(rs.getString("user_name"), rs.getInt("tasks_completed")),
        args.toArray());
  }

  public List<HoursByUserDto> getRealHoursByUser(Long projectId, Long sprintId) {
    StringBuilder sql =
        new StringBuilder(
            """
            SELECT
                u.name AS user_name,
                COALESCE(SUM(i.actual_hours), 0) AS hours
            FROM issues i
            JOIN users u ON u.id = i.assigned_to
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

    sql.append(" GROUP BY u.name ORDER BY u.name");

    return jdbc.query(
        sql.toString(),
        (rs, rowNum) -> new HoursByUserDto(rs.getString("user_name"), rs.getBigDecimal("hours")),
        args.toArray());
  }

  public List<HoursByUserDto> getEstimatedHoursByUser(Long projectId, Long sprintId) {
    StringBuilder sql =
        new StringBuilder(
            """
            SELECT
                u.name AS user_name,
                COALESCE(SUM(i.estimated_hours), 0) AS hours
            FROM issues i
            JOIN users u ON u.id = i.assigned_to
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

    sql.append(" GROUP BY u.name ORDER BY u.name");

    return jdbc.query(
        sql.toString(),
        (rs, rowNum) -> new HoursByUserDto(rs.getString("user_name"), rs.getBigDecimal("hours")),
        args.toArray());
  }
}
