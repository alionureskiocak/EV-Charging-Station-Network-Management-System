package com.example.fse_project.data.repository

import com.example.fse_project.data.local.database.AppDao
import com.example.fse_project.data.local.database.entity.ReservationStatus
import com.example.fse_project.data.mapper.toDomain
import com.example.fse_project.data.mapper.toEntity
import com.example.fse_project.domain.model.Reservation
import com.example.fse_project.domain.repository.ReservationRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ViewModelScoped
class ReservationRepositoryImpl @Inject constructor(
    private val dao : AppDao
) : ReservationRepository{
    override suspend fun createReservation(reservation: Reservation): Long {
        return dao.insertReservation(reservation.toEntity())
    }

    override suspend fun deleteReservation(reservationId: Int) {
       dao.deleteReservation(reservationId)
    }

    override suspend fun updateReservationStatus(
        reservationId: Int,
        newStatus: ReservationStatus
    ) {
        dao.updateReservationStatus(reservationId,newStatus)
    }

    override suspend fun getReservationById(reservationId: Int): Reservation {
        return dao.getReservationById(reservationId).toDomain()
    }

    override fun getAllReservations(): Flow<List<Reservation>> {
        return dao.getReservations().map {
            it.map {
                it.toDomain()
            }
        }
    }

    override fun getAllReservationsByUserId(userId: Int): Flow<List<Reservation>> {
        return dao.getReservationsByUserId(userId).map {
            it.map {
                it.toDomain()
            }
        }
    }

    override fun getAllReservationsByVehicleId(vehicleId: Int): Flow<List<Reservation>> {
        return dao.getReservationsByVehicleId(vehicleId).map {
            it.map {
                it.toDomain()
            }
        }
    }

    override fun getAllReservationsByChargerId(chargerId: Int): Flow<List<Reservation>> {
        return dao.getReservationsByChargerId(chargerId).map {
            it.map {
                it.toDomain()
            }
        }
    }
}