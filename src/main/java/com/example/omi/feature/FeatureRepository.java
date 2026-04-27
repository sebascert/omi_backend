package com.example.omi.feature;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class FeatureRepository {

  private final JdbcTemplate jdbc;

  public FeatureRepository(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  public void create(Long sprintId, CreateFeatureRequest req) {
    String sql =
        """
        INSERT INTO feature (
            id,
            title,
            description,
            sprint_id,
            priority,
            status
        ) VALUES (
            (SELECT COALESCE(MAX(id), 0) + 1 FROM feature),
            ?, ?, ?, ?, ?
        )
        """;

    jdbc.update(
        sql, req.getTitle(), req.getDescription(), sprintId, req.getPriority(), req.getStatus());
  }

  public List<FeatureDto> findBySprint(Long sprintId) {
    String sql =
        """
        SELECT id, title, description, sprint_id, priority, status
        FROM feature
        WHERE sprint_id = ?
        ORDER BY id
        """;

    return jdbc.query(
        sql,
        (rs, rowNum) ->
            new FeatureDto(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getLong("sprint_id"),
                rs.getString("priority"),
                rs.getString("status")),
        sprintId);
  }

  public void delete(Long sprintId, Long featureId) {

    Integer count =
        jdbc.queryForObject(
            "SELECT COUNT(*) FROM feature WHERE id = ? AND sprint_id = ?",
            Integer.class,
            featureId,
            sprintId);

    if (count == null || count == 0) {
      throw new org.springframework.dao.EmptyResultDataAccessException(1);
    }

    jdbc.update(
        """
        DELETE FROM timelog
        WHERE issue_id IN (
            SELECT id FROM issues WHERE feature_id = ?
        )
        """,
        featureId);

    jdbc.update(
        """
        DELETE FROM issue_log
        WHERE issue_id IN (
            SELECT id FROM issues WHERE feature_id = ?
        )
        """,
        featureId);

    jdbc.update("DELETE FROM issues WHERE feature_id = ?", featureId);

    int rows =
        jdbc.update("DELETE FROM feature WHERE id = ? AND sprint_id = ?", featureId, sprintId);

    if (rows == 0) {
      throw new org.springframework.dao.EmptyResultDataAccessException(1);
    }
  }
}
