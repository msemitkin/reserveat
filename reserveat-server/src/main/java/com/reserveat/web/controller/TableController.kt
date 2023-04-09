package com.reserveat.web.controller

import com.reserveat.domain.Table
import com.reserveat.service.TableService
import com.reserveat.web.api.TableApi
import com.reserveat.web.mapper.toTableOutputDto
import com.reserveat.web.model.TableInputDto
import com.reserveat.web.model.TableOutputDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
open class TableController(
    private val tableService: TableService
) : TableApi {

    override fun createTable(
        locationId: Int,
        tableInputDto: TableInputDto
    ): ResponseEntity<TableOutputDto> {
        val table = tableService.createTable(Table(null, locationId, tableInputDto.numberOfSeats))
        val responseDto = table.toTableOutputDto()
        return ResponseEntity.ok(responseDto)
    }

    override fun getLocationTables(locationId: Int): ResponseEntity<MutableList<TableOutputDto>> {
        val tables = tableService.getTablesByLocationId(locationId)
        val responseDto = tables.map(Table::toTableOutputDto).toMutableList()
        return ResponseEntity.ok(responseDto)
    }
}
