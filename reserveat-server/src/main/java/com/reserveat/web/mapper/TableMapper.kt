package com.reserveat.web.mapper

import com.reserveat.domain.Table
import com.reserveat.domain.dto.TableOutputDto
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

class TableMapper : RowMapper<Table> {
    override fun mapRow(rs: ResultSet, rowNum: Int): Table {
        return Table(
            id = rs.getLong("id"),
            locationId = rs.getLong("location_id"),
            numberOfSeats = rs.getInt("number_of_seats")
        )
    }
}

object TableInputMapper {
    fun toTable(numberOfSeats: Int): Table {
        return Table(
            id = 0,
            locationId = 0,
            numberOfSeats = numberOfSeats
        )
    }
}

object TableOutputMapper {
    fun fromTable(table: Table): TableOutputDto {
        return TableOutputDto(
            id = table.id,
            locationId = table.locationId,
            numberOfSeats = table.numberOfSeats
        )
    }
}