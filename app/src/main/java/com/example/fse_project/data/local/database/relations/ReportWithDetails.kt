package com.example.fse_project.data.local.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.fse_project.data.local.database.entities.ReportErrorEntity
import com.example.fse_project.data.local.database.entities.UserEntity
import com.example.fse_project.data.local.database.entities.StationEntity

data class ReportWithDetails(
    @Embedded val report: ReportErrorEntity,

    @Relation(parentColumn = "userId", entityColumn = "id")
    val user: UserEntity,

    @Relation(parentColumn = "stationId", entityColumn = "id")
    val station: StationEntity
)