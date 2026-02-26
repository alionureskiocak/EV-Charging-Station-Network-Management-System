package com.example.fse_project.data.local.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(
    tableName = "reservations",
    foreignKeys = [ForeignKey(
        entity = UserEntity::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    ),
        ForeignKey(
            entity = VehicleEntity::class,
            parentColumns = ["id"],
            childColumns = ["vehicleId"],
            onDelete = ForeignKey.CASCADE),
        ForeignKey(
            entity = ChargerEntity::class,
            parentColumns = ["id"],
            childColumns = ["chargerId"],
            onDelete = ForeignKey.CASCADE)
    ]
    )
data class ReservationEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Int,
    val userId : Int, //user
    val vehicleId : Int, //vehicle
    val chargerId : Int, //charger
    val startTime : LocalDateTime,
    val endTime : LocalDateTime,
    val status : ReservationStatus
)

enum class ReservationStatus{
    AVAILABLE,
    ACTIVE,
    COMPLETED,
    CANCELLED
}