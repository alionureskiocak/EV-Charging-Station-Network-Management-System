package com.example.fse_project.data.local.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "cars",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["ownerId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CarEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Int,
    val ownerId : Int,
    val brand : String,
    val model : String,
    val capacity : Int,
    val connectorType: ConnectorType,
    val licensePlate : String,
)