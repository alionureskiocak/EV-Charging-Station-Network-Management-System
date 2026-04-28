package com.example.fse_project.domain.model

import com.example.fse_project.data.local.database.entities.ChargerStatus

data class Station(
    val id : Long,
    val name : String,
    val latitude : Double,
    val longitude : Double,
    val address : String,
    val isFavorite : Boolean,
    val chargers : List<Charger> = emptyList(),
){
    val status: StationStatus
        get() {
            return when {
                chargers.isEmpty() -> StationStatus.OFFLINE
                chargers.any { it.chargerStatus == ChargerStatus.AVAILABLE } -> StationStatus.AVAILABLE
                chargers.any { it.chargerStatus == ChargerStatus.OCCUPIED } -> StationStatus.OCCUPIED
                chargers.any{ it.chargerStatus == ChargerStatus.FULL } -> StationStatus.FULL
                else -> StationStatus.OFFLINE
            }
        }
}

enum class StationStatus{
    AVAILABLE,
    OCCUPIED,
    FULL,
    OFFLINE
}
