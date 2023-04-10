package com.reserveat.repository

import com.reserveat.domain.Table
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component
import java.sql.ResultSet

//Component annotation is used here to override default Spring CGLIB proxy used for @Repository's
@Component
class TableRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    fun save(table: Table): Table {
        return jdbcTemplate.queryForObjectSafely(
            """
                INSERT INTO "table" (location_id, number_of_seats)
                VALUES (:location_id, :number_of_seats)
                RETURNING id
                """,
            mapOf(
                "location_id" to table.locationId,
                "number_of_seats" to table.numberOfSeats
            )
        ) { rs, _ -> table.copy(id = rs.getInt("id")) }!!
    }

    fun findByLocationId(locationId: Int): List<Table> {
        val queryForStream = jdbcTemplate.queryForStream(
            """
                SELECT * FROM "table"
                WHERE location_id = :location_id 
                """,
            mapOf("location_id" to locationId),
            TableRowMapper
        )
        queryForStream.use {
            return queryForStream.toList()
        }
    }

    fun findById(tableId: Int): Table? {
        return jdbcTemplate.queryForObjectSafely(
            """
            SELECT * FROM "table" WHERE id = :id
            """,
            mapOf("id" to tableId),
            TableRowMapper
        )
    }
}

object TableRowMapper : RowMapper<Table> {
    override fun mapRow(rs: ResultSet, rowNum: Int): Table =
        Table(
            id = rs.getInt("id"),
            locationId = rs.getInt("location_id"),
            numberOfSeats = rs.getInt("number_of_seats")
        )
}
