package com.example.omi.overdue;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OverdueReportRepository {

  private final JdbcTemplate jdbc;

  public OverdueReportRepository(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  public List<OverdueReportDto> findAll() {
    String sql =
        """
        SELECT
            id,
            issue_id,
            generated_at,
            title,
            notes
        FROM overdue_report
        ORDER BY id
        """;

    return jdbc.query(sql, this::mapOverdueReportDto);
  }

  public OverdueReportDto findById(Long reportId) {
    String sql =
        """
        SELECT
            id,
            issue_id,
            generated_at,
            title,
            notes
        FROM overdue_report
        WHERE id = ?
        """;

    return jdbc.queryForObject(sql, this::mapOverdueReportDto, reportId);
  }

  public void create(CreateOverdueReportRequest req) {
    String sql =
        """
        INSERT INTO overdue_report (
            id,
            issue_id,
            generated_at,
            title,
            notes
        ) VALUES (
            (SELECT COALESCE(MAX(id), 0) + 1 FROM overdue_report),
            ?,
            SYSTIMESTAMP,
            ?,
            ?
        )
        """;

    jdbc.update(sql, req.getIssueId(), req.getTitle(), req.getNotes());
  }

  public void update(Long reportId, UpdateOverdueReportRequest req) {
    String sql =
        """
        UPDATE overdue_report
        SET
            issue_id = ?,
            title = ?,
            notes = ?
        WHERE id = ?
        """;

    int rows = jdbc.update(sql, req.getIssueId(), req.getTitle(), req.getNotes(), reportId);

    if (rows == 0) {
      throw new org.springframework.dao.EmptyResultDataAccessException(1);
    }
  }

  public void delete(Long reportId) {
    int rows = jdbc.update("DELETE FROM overdue_report WHERE id = ?", reportId);

    if (rows == 0) {
      throw new org.springframework.dao.EmptyResultDataAccessException(1);
    }
  }

  public boolean issueExists(Long issueId) {
    Integer count =
        jdbc.queryForObject("SELECT COUNT(*) FROM issues WHERE id = ?", Integer.class, issueId);

    return count != null && count > 0;
  }

  private OverdueReportDto mapOverdueReportDto(ResultSet rs, int rowNum) throws SQLException {
    return new OverdueReportDto(
        rs.getLong("id"),
        rs.getLong("issue_id"),
        rs.getObject("generated_at", OffsetDateTime.class),
        rs.getString("title"),
        rs.getString("notes"));
  }
}
