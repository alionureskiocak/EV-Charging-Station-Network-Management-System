package com.example.fse_project.data.repository

import com.example.fse_project.data.local.database.AppDao
import com.example.fse_project.data.mapper.toDomain
import com.example.fse_project.data.mapper.toEntity
import com.example.fse_project.domain.model.Charger
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
    override suspend fun createStation(station: Station): Long {
        return dao.insertStation(station.toEntity())
    }

    override suspend fun deleteStation(stationId : Int) {
        dao.deleteStation(stationId)
    }

    override suspend fun getStationById(stationId: Int): Station {
        return dao.getStationById(stationId).toDomain()
    }

    override suspend fun getStationByChargerId(chargerId: Int): Station {
        return dao.getStationByChargerId(chargerId).toDomain()
    }

    override fun getAllStations(): Flow<List<Station>> {
        return dao.getStations().map {
            it.map {
                it.toDomain()
            }
        }
    }

    override suspend fun createCharger(charger: Charger): Long {
        return dao.insertCharger(charger.toEntity())
    }

    override suspend fun deleteCharger(chargerId : Int) {
        dao.deleteCharger(chargerId)
    }

    override suspend fun getChargerById(chargerId: Int): Charger {
        return dao.getChargerById(chargerId).toDomain()
    }

    override fun getChargersByStation(stationId: Int): Flow<List<Charger>> {
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