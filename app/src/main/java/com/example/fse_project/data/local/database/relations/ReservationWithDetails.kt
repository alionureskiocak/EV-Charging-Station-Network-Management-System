package com.example.fse_project.data.local.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.fse_project.data.local.database.entities.ChargerEntity
import com.example.fse_project.data.local.database.entities.ReservationEntity
import com.example.fse_project.data.local.database.entities.StationEntity
import com.example.fse_project.data.local.database.entities.UserEntity
import com.example.fse_project.data.local.database.entities.VehicleEntity
import com.example.fse_project.domain.model.Reservation

data class ReservationWithDetails(

    @Embedded
    val reservation : ReservationEntity,

    @Relation(
        parentColumn = "userId",
        entityColumn = "id"
    )
    val user : UserEntity,

    @Relation(
        parentColumn = "vehicleId",
        entityColumn = "id"
    )
    val vehicle : VehicleEntity,

    @Relation(
        parentColumn = "stationId",
        entityColumn = "id"
    )
    val station : StationEntity,

    @Relation(
        parentColumn = "chargerId",
        entityColumn = "id"
    )
    val charger : ChargerEntity


)
