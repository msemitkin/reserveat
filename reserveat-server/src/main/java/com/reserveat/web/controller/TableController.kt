package com.reserveat.web.controller

import com.reserveat.domain.dto.*
import com.reserveat.repository.TableRepository
import com.reserveat.service.TableService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/tables")
class TableController(
    private val tableService: TableService,
    private val tableRepository: TableRepository
) : TableApi {
    @PostMapping("/locations/{locationId}")
    fun createTable(
        @PathVariable locationId: Long,
        @RequestBody tableInputDto: TableInputDto
    ): ResponseEntity<TableOutputDto> {
        val table = tableService.createTable(locationId, tableInputDto.toTable())
        val responseDto = table.toTableOutputDto()
        return ResponseEntity.ok(responseDto)
    }

    @PutMapping("/{tableId}")
    fun updateTable(
        @PathVariable tableId: Long,
        @RequestBody tableInputDto: TableInputDto
    ): ResponseEntity<TableOutputDto> {
        val table = tableService.updateTable(tableId, tableInputDto.toTable())
        val responseDto = table.toTableOutputDto()
        return ResponseEntity.ok(responseDto)
    }

    @GetMapping("/{locationId}")
    fun getTablesByLocationId(
        @PathVariable locationId: Long
    ): ResponseEntity<List<TableOutputDto>> {
        val tables = tableService.getTablesByLocationId(locationId)
        val responseDto = tables.map { tableOutputDto ->
            tableOutputDto
        }
        return ResponseEntity.ok(responseDto)
    }
}
