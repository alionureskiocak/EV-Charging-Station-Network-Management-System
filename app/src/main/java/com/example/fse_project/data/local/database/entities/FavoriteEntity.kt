package com.example.fse_project.data.local.database.entities

import androidx.room.Entity

@Entity(
    tableName = "favorites",
    primaryKeys = ["userId", "stationId"]
)
data class FavoriteEntity(
    val userId : Long,
    val stationId : Long
)
