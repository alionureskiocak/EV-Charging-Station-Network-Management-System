package com.example.fse_project.domain.model

import com.example.fse_project.data.local.database.entities.ReservationStatus
import java.time.LocalDateTime

data class Reservation(
    val id : Long,
    val user : User,
    val vehicle : Vehicle, //vehicle
    val station : Station, //station
    val charger : Charger, //charger
    val startTime : LocalDateTime,
    val endTime : LocalDateTime,
    val pricePerKwh : Double,
    var status : ReservationStatus
)
