package com.example.fse_project.domain.repository

import com.example.fse_project.data.local.database.entities.ReservationStatus
import com.example.fse_project.domain.model.Reservation
import kotlinx.coroutines.flow.Flow

interface ReservationRepository {

    suspend fun createReservation(reservation: Reservation): Long
    suspend fun deleteReservation(reservationId: Long)
    suspend fun updateReservationStatus(reservationId: Long, newStatus: ReservationStatus)
    suspend fun getReservationById(reservationId: Long): Reservation
    fun getAllReservations(): Flow<List<Reservation>>
    fun getAllReservationsByUserId(userId: Long): Flow<List<Reservation>>
    fun getAllReservationsByVehicleId(vehicleId: Long): Flow<List<Reservation>>
    fun getAllReservationsByChargerId(chargerId: Long): Flow<List<Reservation>>
}