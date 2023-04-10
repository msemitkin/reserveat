package com.reserveat.repository

import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

fun <T> NamedParameterJdbcTemplate.queryForObjectSafely(
    sql: String,
    paramMap: Map<String, Any>,
    rowMapper: RowMapper<T>
): T? {
    return try {
        this.queryForObject(sql, paramMap, rowMapper)
    } catch (ignored: EmptyResultDataAccessException) {
        null
    }
}
