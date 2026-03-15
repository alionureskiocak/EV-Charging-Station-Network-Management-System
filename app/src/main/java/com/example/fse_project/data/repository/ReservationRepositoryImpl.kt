package com.example.fse_project.data.repository

import com.example.fse_project.data.local.database.AppDao
import com.example.fse_project.data.local.database.entities.ReservationStatus
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
        println("repoda da yaptım")
    }

    override suspend fun deleteReservation(reservationId: Long) {
       dao.deleteReservation(reservationId)
    }

    override suspend fun updateReservationStatus(
        reservationId: Long,
        newStatus: ReservationStatus
    ) {
        dao.updateReservationStatus(reservationId,newStatus)
    }

    override suspend fun getReservationById(reservationId: Long): Reservation {
        return dao.getReservationDetailsById(reservationId).toDomain()
    }

    override fun getAllReservations(): Flow<List<Reservation>> {
        return dao.getAllReservations().map {
            it.map {
                it.toDomain()
            }
        }
    }

    override fun getAllReservationsByUserId(userId: Long): Flow<List<Reservation>> {
        return dao.getReservationDetailsByUserId(userId).map {
            it.map {
                it.toDomain()
            }
        }
    }

    override fun getAllReservationsByVehicleId(vehicleId: Long): Flow<List<Reservation>> {
        return dao.getReservationDetailsByVehicleId(vehicleId).map {
            it.map {
                it.toDomain()
            }
        }
    }

    override fun getAllReservationsByChargerId(chargerId: Long): Flow<List<Reservation>> {
        return dao.getReservationDetailsByChargerId(chargerId).map {
            it.map {
                it.toDomain()
            }
        }
    }
}