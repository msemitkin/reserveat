package com.reserveat.service

import com.reserveat.domain.Table
import com.reserveat.repository.TableRepository
import org.springframework.stereotype.Service

@Service
class TableService(
    private val tableRepository: TableRepository
) {
    fun createTable(
        table: Table
    ): Table {
        return tableRepository.save(table)
    }

    fun getTablesByLocationId(
        locationId: Int
    ): List<Table> {
        return tableRepository.findByLocationId(locationId)
    }
}
