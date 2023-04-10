package com.reserveat.service

import com.reserveat.domain.Reservation
import com.reserveat.domain.Table
import com.reserveat.repository.LocationRepository
import com.reserveat.repository.ReservationRepository
import com.reserveat.repository.TableRepository
import com.reserveat.web.exception.ReservationConflictException
import com.reserveat.web.exception.ResourceNotFoundException
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

@Service
open class ReservationService(
    private val reservationRepository: ReservationRepository,
    private val tableRepository: TableRepository,
    private val locationRepository: LocationRepository
) {
    companion object {
        val MAX_DURATION: Duration = Duration.ofHours(12)
    }

    fun createReservation(reservation: Reservation): Reservation {
        validateReservation(reservation);
        return reservationRepository.save(reservation)
    }

    fun getReservationById(reservationId: Int): Reservation? {
        return reservationRepository.findById(reservationId)
    }

    fun deleteReservation(reservationId: Int) {
        reservationRepository.deleteById(reservationId)
    }

    private fun validateReservation(reservation: Reservation) {
        val table = tableRepository.findById(reservation.tableId)
            ?: throw ResourceNotFoundException("table does not exist")
        val slot = reservation.getSlot()

        validateReservationEndsOnTheDayOfStart(slot)
        validateDurationDoesNotExceedMaxAllowed(slot)
        validateSlotIsFree(slot, table)

        reservationRepository.save(reservation)
    }

    private fun validateReservationEndsOnTheDayOfStart(slot: Slot) {
        if (!slot.from.toLocalDate().equals(slot.to.toLocalDate())) {
            throw RuntimeException("Reservation must start and end on the same day")
        }
    }

    private fun validateDurationDoesNotExceedMaxAllowed(slot: Slot) {
        if (slot.getDuration() > MAX_DURATION) {
            throw RuntimeException("Reservation is too long")
        }
    }

    private fun validateSlotIsFree(slot: Slot, table: Table) {
        val forDate = slot.from.toLocalDate()
        val freeSlots = getFreeSlots(table, forDate)
        if (freeSlots.none { it.contains(slot) }) {
            cannotBeReserved()
        }
    }

    private fun getFreeSlots(table: Table, forDate: LocalDate): List<Slot> {
        val location = locationRepository.findById(table.locationId).get()
        val dayWorkingHours = location.workingHours[forDate.dayOfWeek] ?: cannotBeReserved()

        val reservations = getReservations(table.id!!, forDate)
            .sortedBy(Reservation::startTime)

        val freeSlots = ArrayList<Slot>()
        var start = LocalDateTime.of(forDate, dayWorkingHours.from);
        var index = 0
        while (index < reservations.size) {
            val reservation = reservations[index]
            if (!start.equals(reservation.startTime)) {
                freeSlots += Slot(start, reservation.startTime)
            }
            start = reservation.endTime
            index++
        }
        if (!start.equals(dayWorkingHours.to)) {
            freeSlots += Slot(start, LocalDateTime.of(forDate, dayWorkingHours.to))
        }
        return freeSlots
    }

    private fun getReservations(tableId: Int, forDate: LocalDate): List<Reservation> {
        return reservationRepository.findAll(tableId, forDate)
    }

    private fun cannotBeReserved(): Nothing {
        throw ReservationConflictException("Table cannot be reserved at specified time period")
    }
}

data class Slot(val from: LocalDateTime, val to: LocalDateTime) {
    fun contains(slot: Slot) = !this.from.isAfter(slot.from) && !slot.to.isAfter(this.to)
}

fun Reservation.getSlot() = Slot(this.startTime, this.endTime)

fun Slot.getDuration(): Duration = Duration.between(this.from, this.to)
