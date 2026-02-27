package com.example.fse_project.domain.repository

import com.example.fse_project.data.local.database.entity.ReservationStatus
import com.example.fse_project.domain.model.Reservation
import kotlinx.coroutines.flow.Flow

interface ReservationRepository {

    suspend fun createReservation(reservation: Reservation): Long
    suspend fun deleteReservation(reservationId: Int)
    suspend fun updateReservationStatus(reservationId: Int, newStatus: ReservationStatus)
    suspend fun getReservationById(reservationId: Int): Reservation
    fun getAllReservations(): Flow<List<Reservation>>
    fun getAllReservationsByUserId(userId: Int): Flow<List<Reservation>>
    fun getAllReservationsByVehicleId(vehicleId: Int): Flow<List<Reservation>>
    fun getAllReservationsByChargerId(chargerId: Int): Flow<List<Reservation>>
}