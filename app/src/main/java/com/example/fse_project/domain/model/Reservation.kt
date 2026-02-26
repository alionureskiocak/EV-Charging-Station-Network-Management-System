package com.example.fse_project.domain.model

import com.example.fse_project.data.local.database.entity.ReservationStatus
import java.time.LocalDateTime

data class Reservation(
    val id : Int,
    val userId : Int, //user
    val vehicleId : Int, //vehicle
    val chargerId : Int, //charger
    val startTime : LocalDateTime,
    val endTime : LocalDateTime,
    val status : ReservationStatus
)
