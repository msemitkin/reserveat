package com.reserveat.repository

import com.reserveat.domain.Table
import com.reserveat.domain.dto.TableDto
import com.reserveat.domain.dto.TableOutputDto
import com.reserveat.web.mapper.TableMapper
import com.reserveat.web.mapper.TableOutputMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import java.sql.Statement

@Repository
class TableRepository(
    private val jdbcTemplate: JdbcTemplate
) {
    fun save(
        locationId: Long, table: Table
    ): Table {
        val keyHolder = GeneratedKeyHolder()
        jdbcTemplate.update({ connection ->
            val ps = connection.prepareStatement(
                    """
                INSERT INTO tables (location_id, number_of_seats)
                VALUES (?, ?)
                """,
                    Statement.RETURN_GENERATED_KEYS
            )
            ps.setLong(1, locationId)
            ps.setInt(2, table.numberOfSeats)
            ps
        }, keyHolder)
        val tableId = keyHolder.key!!.toLong()
        return table.copy(id = tableId)
    }

    fun update(
        table: Table
    ): Table {
        jdbcTemplate.update(
                """
            UPDATE tables
            SET number_of_seats = ?
            WHERE id = ?
            """,
                table.numberOfSeats,
                table.id
        )
        return table
    }

    fun findByLocationId(
        locationId: Long
    ): List<TableOutputDto> {
        return jdbcTemplate.query(
                """
        SELECT * FROM tables
        WHERE location_id = ?
        """,
                TableMapper(),
                locationId
        ).map { table -> TableOutputMapper.fromTable(table) }
    }

}
