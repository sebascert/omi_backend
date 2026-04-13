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
}
