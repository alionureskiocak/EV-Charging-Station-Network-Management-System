package com.example.fse_project.domain.model

import com.example.fse_project.data.local.database.entities.ReservationStatus
import java.time.LocalDateTime

data class Reservation(
    val id : Long,
    val userId : Long, //user
    val vehicleId : Long, //vehicle
    val chargerId : Long, //charger
    val startTime : LocalDateTime,
    val endTime : LocalDateTime,
    val pricePerKwh : Double,
    val status : ReservationStatus
)
