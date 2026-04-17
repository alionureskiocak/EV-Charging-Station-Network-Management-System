package com.example.fse_project.data.local.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "chargers",
    foreignKeys = [
        ForeignKey(
            entity = StationEntity::class,
            parentColumns = ["id"],
            childColumns = ["stationOwnerId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ChargerEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Long,
    val stationOwnerId : Long, //station
    val chargerName : String,
    val chargerType: ChargerType,
    val powerOutput: PowerOutput,
    val connectorType: ConnectorType,
    val chargerStatus: ChargerStatus
)

enum class ChargerType{
    AC,
    DC
}

enum class PowerOutput{
    KW_22,
    KW_50,
    KW_150
}

enum class ConnectorType{
    TYPE_2,
    CCS,
    CHADEMO
}

enum class ChargerStatus{
    AVAILABLE,
    OCCUPIED,
    FULL,
    OFFLINE
}