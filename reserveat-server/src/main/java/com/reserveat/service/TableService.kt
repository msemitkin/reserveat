package com.reserveat.service

import com.reserveat.domain.Table
import com.reserveat.domain.dto.TableOutputDto
import com.reserveat.repository.TableRepository
import org.springframework.stereotype.Service

@Service
class TableService(
    private val tableRepository: TableRepository
) {
    fun createTable(
       locationId: Long, table: Table
    ): Table {
        val table = Table(
            id = table.id,
            locationId = locationId,
            numberOfSeats = table.numberOfSeats
        )
        return tableRepository.save(locationId, table)
    }

    fun updateTable(
       tableId: Long, table: Table
    ): Table {
        val table = Table(
            id = tableId,
            locationId = table.locationId,
            numberOfSeats = table.numberOfSeats
        )
        return tableRepository.update(table)
    }

    fun getTablesByLocationId(
        locationId: Long
    ): List<TableOutputDto> {
        return tableRepository.findByLocationId(locationId)
    }
}
