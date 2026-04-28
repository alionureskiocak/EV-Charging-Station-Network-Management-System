package com.example.fse_project.data.repository

import co.yml.charts.common.extensions.isNotNull
import com.example.fse_project.data.local.database.AppDao
import com.example.fse_project.data.local.database.entities.ChargerStatus
import com.example.fse_project.data.local.database.relations.StationWithChargers
import com.example.fse_project.data.mapper.toDomain
import com.example.fse_project.data.mapper.toEntity
import com.example.fse_project.data.mapper.toStation
import com.example.fse_project.domain.model.Charger
import com.example.fse_project.domain.model.Favorite
import com.example.fse_project.domain.model.Station
import com.example.fse_project.domain.repository.StationRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ViewModelScoped
class StationRepositoryImpl @Inject constructor(
    private val dao : AppDao
) : StationRepository{

    override fun getStationDomainModels(): Flow<List<Station>> {
        return dao.getStationWithChargers().map {
            it.map {
                it.toStation()
            }
        }
    }

    override suspend fun createStation(station: Station): Long {
        val stationId =  dao.insertStation(station.toEntity())

        val chargers = station.chargers.map {
            it.toEntity().copy(
                id = 0,
                stationOwnerId = stationId
            )
        }
        if (chargers.isNotEmpty()){
            chargers.forEach { charger ->
                dao.insertCharger(charger)
            }
        }
        return stationId
    }

    override suspend fun deleteStation(stationId : Long) {
        dao.deleteStation(stationId)
    }

    override suspend fun getStationById(stationId: Long): Station {
        return dao.getStationById(stationId).toDomain()
    }

    override suspend fun getStationByChargerId(chargerId: Long): Station {
        return dao.getStationByChargerId(chargerId).toDomain()
    }

    override fun getAllStations(): Flow<List<Station>> {
        return dao.getStations().map {
            it.map {
                it.toDomain()
            }
        }
    }

    override fun getStations(): Flow<List<Station>> {
        return dao.getStationWithChargers().map {
            it.map { it.toStation() }
        }
    }

    override suspend fun updateChargerStatus(
        id: Long,
        status: ChargerStatus
    ) {
        dao.updateChargerStatus(id,status)
    }

    override suspend fun addFavorites(favorite: Favorite) {
        val favoriteEntity = favorite.toEntity()
        dao.addFavorites(favoriteEntity)
    }

    override suspend fun removeFavorites(userId: Long, stationId: Long) {
        dao.removeFavorites(userId,stationId)
    }

    override fun isStationFavorite(
        userId: Long,
        stationId: Long
    ): Flow<Boolean> {
       return dao.isStationFavorite(userId,stationId)
    }

    override fun getFavoritesByUser(userId: Long): Flow<List<Favorite>> {
        return dao.getFavoriteStationsByUser(userId).map { it.map { it.toDomain() } }
    }

    override suspend fun createCharger(charger: Charger): Long {
        return dao.insertCharger(charger.toEntity())
    }

    override suspend fun deleteCharger(chargerId : Long) {
        dao.deleteCharger(chargerId)
    }

    override suspend fun getChargerById(chargerId: Long): Charger {
        return dao.getChargerById(chargerId).toDomain()
    }

    override fun getChargersByStation(stationId: Long): Flow<List<Charger>> {
        return dao.getChargersByStation(stationId).map {
            it.map {
                it.toDomain()
            }
        }
    }

    override fun getAllChargers(): Flow<List<Charger>> {
        return dao.getChargers().map {
            it.map {
                it.toDomain()
            }
        }
    }
}