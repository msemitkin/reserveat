package com.reserveat.repository

import com.reserveat.domain.Reservation
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.sql.Timestamp
import java.time.LocalDateTime

@Repository
class ReservationRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    fun save(reservation: Reservation): Reservation {
        return jdbcTemplate.queryForObject(
            """
            INSERT INTO reservation (location_id, start_time, end_time, number_of_people, table_id)
            VALUES (:location_id, :start_time, :end_time, :number_of_people, :table_id)
            RETURNING id
            """,
            mapOf(
                "location_id" to reservation.locationId,
                "start_time" to reservation.startTime,
                "end_time" to reservation.endTime,
                "number_of_people" to reservation.numberOfPeople,
                "table_id" to reservation.table.id
            )
        ) { rs, _ -> reservation.copy(id = rs.getInt("id")) }!!
    }

    fun findById(reservationId: Int): Reservation? {
        return jdbcTemplate.query(
            """
            SELECT * FROM reservation r
            JOIN "table" t ON r.table_id = t.id
            WHERE r.id = :id
            """,
            mapOf("id" to reservationId),
            ReservationRowMapper
        ).firstOrNull()
    }

    fun existsById(reservationId: Int): Boolean {
        return jdbcTemplate.query(
            """
            SELECT EXISTS (
                SELECT 1 FROM reservation WHERE id = :id
            )
            """,
            mapOf("id" to reservationId),
            ResultSetExtractor { rs -> rs.getBoolean(1) }
        )
    }

    fun update(reservation: Reservation): Reservation {
        jdbcTemplate.update(
            """
            UPDATE reservation SET location_id = :location_id, start_time = :start_time, end_time = :end_time,
            number_of_people = :number_of_people, table_id = :table_id
            WHERE id = :id
            """,
            mapOf(
                "id" to reservation.id,
                "location_id" to reservation.locationId,
                "start_time" to reservation.startTime,
                "end_time" to reservation.endTime,
                "number_of_people" to reservation.numberOfPeople,
                "table_id" to reservation.table.id
            )
        )
        return reservation
    }

    fun deleteById(reservationId: Int) {
        jdbcTemplate.update(
            """
            DELETE FROM reservation
            WHERE id = :id
            """,
            mapOf("id" to reservationId)
        )
    }

    fun findByTableIdAndStartTime(tableId: Int, startTime: LocalDateTime): Reservation? {
        return jdbcTemplate.query(
            """
            SELECT * FROM reservation r
            JOIN "table" t ON r.table_id = t.id
            WHERE r.table_id = :table_id AND r.start_time = :start_time
            """,
            mapOf("table_id" to tableId, "start_time" to startTime),
            ReservationRowMapper
        ).firstOrNull()
    }

    fun findByLocationIdAndStartTime(locationId: Int, startTime: LocalDateTime): Reservation? {
        return jdbcTemplate.query(
            """
            SELECT * FROM reservation r
            JOIN "table" t ON r.table_id = t.id
            WHERE r.table_id = :table_id AND r.start_time = :start_time
            """,
            mapOf("location_id" to locationId, "start_time" to startTime),
            ReservationRowMapper
        ).firstOrNull()
    }
}

object ReservationRowMapper : RowMapper<Reservation> {
    override fun mapRow(rs: ResultSet, rowNum: Int): Reservation =
        Reservation(
            id = rs.getInt("id"),
            locationId = rs.getInt("location_id"),
            startTime = rs.getTimestamp("start_time").toLocalDateTime(),
            endTime = rs.getTimestamp("end_time").toLocalDateTime(),
            numberOfPeople = rs.getInt("number_of_people"),
            table = TableRowMapper.mapRow(rs, rowNum)
        )
}

