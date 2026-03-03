package com.example.fse_project.domain.model

import com.example.fse_project.data.local.database.entities.ChargerStatus

data class Station(
    val id : Long,
    val name : String,
    val latitude : Double,
    val longitude : Double,
    val address : String,
    val chargers : List<Charger>,
){
    val status : StationStatus
        get(){
            return when{
                chargers.isEmpty() -> StationStatus.OFFLINE
                chargers.any { it.chargerStatus == ChargerStatus.AVAILABLE } -> StationStatus.AVAILABLE
                chargers.all { it.chargerStatus == ChargerStatus.OFFLINE } -> StationStatus.OFFLINE
                chargers.all { it.chargerStatus == ChargerStatus.OCCUPIED } -> StationStatus.OCCUPIED
                else -> {
                    StationStatus.OFFLINE
                }
            }
        }
}

enum class StationStatus{
    AVAILABLE,
    OCCUPIED,
    OFFLINE
}
