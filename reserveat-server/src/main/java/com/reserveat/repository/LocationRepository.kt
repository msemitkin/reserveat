package com.reserveat.repository

import com.reserveat.domain.model.Location
import org.springframework.stereotype.Repository
import org.springframework.data.repository.CrudRepository

@Repository
interface LocationRepository : CrudRepository<Location, String>
