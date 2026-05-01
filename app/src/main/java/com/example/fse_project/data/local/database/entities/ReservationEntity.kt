package com.example.fse_project.data.local.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
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
            entity = StationEntity::class,
            parentColumns = ["id"],
            childColumns = ["stationId"],
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
    val id : Long,
    val userId : Long, //user
    val vehicleId : Long, //vehicle
    val stationId : Long, //station
    val chargerId : Long, //charger
    val startTime : LocalDateTime,
    val endTime : LocalDateTime,
    val pricePerKwh : Double,
    val status : ReservationStatus,
    val actualKwh: Double = 0.0,
    val totalAmount: Double = 0.0
)

enum class ReservationStatus{
    AVAILABLE,
    ACTIVE,
    COMPLETED,
    CANCELLED
}