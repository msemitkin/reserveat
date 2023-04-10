package com.reserveat.service

import com.reserveat.domain.Reservation
import com.reserveat.repository.ReservationRepository
import com.reserveat.repository.TableRepository
import com.reserveat.web.exception.ReservationConflictException
import com.reserveat.web.exception.ResourceNotFoundException
import com.reserveat.web.mapper.ReservationMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class ReservationService(
    private val reservationRepository: ReservationRepository
) {
    @Transactional
    open fun createReservation(reservation: Reservation): Reservation {
        if (reservationRepository.findByLocationIdAndStartTime(
                reservation.locationId, reservation.startTime) != null) {
            throw ReservationConflictException("Reservation conflict")
        }
        return reservationRepository.save(reservation)
    }

    @Transactional(readOnly = true)
    open fun getReservationById(reservationId: Int): Reservation? {
        return reservationRepository.findById(reservationId)
    }

    @Transactional
    open fun updateReservation(reservation: Reservation) {
        if (!reservationRepository.existsById(reservation.id)) {
            throw ResourceNotFoundException("Reservation not found")
        }
        if (reservationRepository.findByLocationIdAndStartTime(
                reservation.locationId, reservation.startTime) != null) {
            throw ReservationConflictException("Reservation conflict")
        }
        reservationRepository.update(reservation)
    }

    @Transactional
    open fun deleteReservation(reservationId: Int) {
        if (!reservationRepository.existsById(reservationId)) {
            throw ResourceNotFoundException("Reservation not found")
        }
        reservationRepository.deleteById(reservationId)
    }
}
