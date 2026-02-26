package com.example.fse_project.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stations")
data class StationEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Long,
    val name : String,
    val latitude : Double,
    val longitude : Double,
    val address : String
)
