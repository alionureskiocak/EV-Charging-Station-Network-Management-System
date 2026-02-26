package com.example.fse_project.data.local.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "vehicles",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["ownerId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class VehicleEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Long,
    val ownerId : Long, //user
    val brand : String,
    val model : String,
    val capacity : Int,
    val connectorType: ConnectorType,
    val licensePlate : String,
)