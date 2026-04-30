package com.example.fse_project.domain.repository

import com.example.fse_project.data.local.database.entities.ChargerStatus
import com.example.fse_project.data.local.database.relations.StationWithChargers
import com.example.fse_project.domain.model.Charger
import com.example.fse_project.domain.model.Favorite
import com.example.fse_project.domain.model.Station
import kotlinx.coroutines.flow.Flow

interface StationRepository {

    fun getStationDomainModels(): Flow<List<Station>>

    suspend fun createStation(station: Station) : Long
    suspend fun deleteStation(stationId : Long)
    suspend fun getStationById(stationId : Long) : Station
    suspend fun getStationByChargerId(chargerId : Long) : Station
    fun getAllStations() : Flow<List<Station>>
    fun getStations() : Flow<List<Station>>

    suspend fun createCharger(charger: Charger) : Long
    suspend fun deleteCharger(chargerId : Long)
    suspend fun getChargerById(chargerId : Long) : Charger
    fun getChargersByStation(stationId : Long) : Flow<List<Charger>>
    fun getAllChargers() : Flow<List<Charger>>
    suspend fun updateChargerStatus(id : Long, status : ChargerStatus)

    suspend fun addFavorites(favorite : Favorite)
    suspend fun removeFavorites(userId : Long, stationId : Long)
    fun isStationFavorite(userId : Long, stationId : Long) : Flow<Boolean>
    fun getFavoritesByUser(userId : Long) : Flow<List<Station>>
}