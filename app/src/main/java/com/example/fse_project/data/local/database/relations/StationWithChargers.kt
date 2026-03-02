package com.example.fse_project.data.local.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.fse_project.data.local.database.entities.StationEntity
import com.example.fse_project.domain.model.Charger

data class StationWithChargers(
    @Embedded
    val station : StationEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "stationOwnerId"
    )
    val chargers : List<Charger>
)