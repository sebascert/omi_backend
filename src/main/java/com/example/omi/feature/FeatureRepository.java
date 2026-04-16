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
}
