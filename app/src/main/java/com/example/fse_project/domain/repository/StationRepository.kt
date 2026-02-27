package com.example.fse_project.domain.repository

import com.example.fse_project.data.local.database.entity.ChargerEntity
import com.example.fse_project.data.local.database.entity.StationEntity
import com.example.fse_project.domain.model.Charger
import com.example.fse_project.domain.model.Station
import kotlinx.coroutines.flow.Flow

interface StationRepository {

    suspend fun createStation(stationEntity: StationEntity) : Long
    suspend fun deleteStation(stationEntity: StationEntity)
    suspend fun getStationById(stationId : Int) : Station
    suspend fun getStationByChargerId(chargerId : Int) : Station
    fun getAllStations() : Flow<List<Station>>

    suspend fun createCharger(chargerEntity: ChargerEntity) : Long
    suspend fun deleteCharger(chargerEntity: ChargerEntity)
    suspend fun getChargerById(chargerId : Int) : Charger
    fun getChargersByStation(stationId : Int) : Flow<List<Charger>>
    fun getAllChargers() : Flow<List<Charger>>
}