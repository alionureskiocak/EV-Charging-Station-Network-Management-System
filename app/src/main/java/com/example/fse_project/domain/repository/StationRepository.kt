package com.example.fse_project.domain.repository

import com.example.fse_project.domain.model.Charger
import com.example.fse_project.domain.model.Station
import kotlinx.coroutines.flow.Flow

interface StationRepository {

    suspend fun createStation(station: Station) : Long
    suspend fun deleteStation(stationId : Int)
    suspend fun getStationById(stationId : Int) : Station
    suspend fun getStationByChargerId(chargerId : Int) : Station
    fun getAllStations() : Flow<List<Station>>

    suspend fun createCharger(charger: Charger) : Long
    suspend fun deleteCharger(chargerId : Int)
    suspend fun getChargerById(chargerId : Int) : Charger
    fun getChargersByStation(stationId : Int) : Flow<List<Charger>>
    fun getAllChargers() : Flow<List<Charger>>
}