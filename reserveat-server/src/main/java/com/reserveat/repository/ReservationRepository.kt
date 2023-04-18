package com.reserveat.repository

import com.reserveat.domain.Reservation
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component
import java.sql.ResultSet
import java.time.LocalDate

//Component annotation is used here to override default Spring CGLIB proxy used for @Repository's
@Component
class ReservationRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    fun save(reservation: Reservation): Reservation {
        return jdbcTemplate.queryForObjectSafely(
            """
            INSERT INTO reservation (table_id, start, "end", reservator_id, number_of_people)
            VALUES (:table_id, :start, :end, :reservator_id, :number_of_people)
            RETURNING id
            """,
            mapOf(
                "table_id" to reservation.tableId,
                "start" to reservation.startTime,
                "end" to reservation.endTime,
                "reservator_id" to -1, //TODO replace with actual user id
                "number_of_people" to reservation.numberOfPeople
            )
        ) { rs, _ -> reservation.copy(id = rs.getInt("id")) }!!
    }

    fun findById(reservationId: Int): Reservation? {
        return jdbcTemplate.queryForObjectSafely(
            "SELECT * FROM reservation r WHERE r.id = :id",
            mapOf("id" to reservationId),
            ReservationRowMapper
        )
    }

    fun findAll(tableId: Int, date: LocalDate): List<Reservation> {
        return jdbcTemplate.queryForStream(
            """
            SELECT r.* FROM reservation r
            WHERE r.table_id = :table_id
            AND DATE(r.start) = :date
            """,
            mapOf(
                "table_id" to tableId,
                "date" to date
            ),
            ReservationRowMapper
        ).use { it.toList() }
    }


    fun deleteById(reservationId: Int) {
        jdbcTemplate.update(
            "DELETE FROM reservation WHERE id = :id",
            mapOf("id" to reservationId)
        )
    }
}

object ReservationRowMapper : RowMapper<Reservation> {
    override fun mapRow(rs: ResultSet, rowNum: Int): Reservation =
        Reservation(
            id = rs.getInt("id"),
            tableId = rs.getInt("table_id"),
            startTime = rs.getTimestamp("start").toLocalDateTime(),
            endTime = rs.getTimestamp("end").toLocalDateTime(),
            numberOfPeople = rs.getInt("number_of_people")
        )
}
