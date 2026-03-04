package com.example.fse_project.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fse_project.data.local.database.entities.ChargerStatus
import com.example.fse_project.data.local.database.entities.ChargerType
import com.example.fse_project.data.local.database.entities.ConnectorType
import com.example.fse_project.data.local.database.entities.PowerOutput
import com.example.fse_project.domain.model.Charger
import com.example.fse_project.domain.model.Station
import com.example.fse_project.domain.repository.StationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repo: StationRepository
) : ViewModel() {

    init {

        val c1 = Charger(
            id = 1,
            stationOwnerId = 1,
            chargerName = "S1C1",
            chargerType = ChargerType.AC,
            powerOutput = PowerOutput.KW_50,
            connectorType = ConnectorType.CHADEMO,
            chargerStatus = ChargerStatus.AVAILABLE
        )
        val c2 = Charger(
            id = 2,
            stationOwnerId = 1,
            chargerName = "S1C2",
            chargerType = ChargerType.DC,
            powerOutput = PowerOutput.KW_22,
            connectorType = ConnectorType.TYPE_2,
            chargerStatus = ChargerStatus.AVAILABLE
        )
        val c3 = Charger(
            id = 3,
            stationOwnerId = 1,
            chargerName = "S1C3",
            chargerType = ChargerType.AC,
            powerOutput = PowerOutput.KW_150,
            connectorType = ConnectorType.CHADEMO,
            chargerStatus = ChargerStatus.OCCUPIED
        )
        val c4 = Charger(
            id = 4,
            stationOwnerId = 1,
            chargerName = "S1C4",
            chargerType = ChargerType.DC,
            powerOutput = PowerOutput.KW_22,
            connectorType = ConnectorType.CCS,
            chargerStatus = ChargerStatus.OFFLINE
        )

        val c5 = Charger(
            id = 5,
            stationOwnerId = 2,
            chargerName = "S2C1",
            chargerType = ChargerType.DC,
            powerOutput = PowerOutput.KW_150,
            connectorType = ConnectorType.TYPE_2,
            chargerStatus = ChargerStatus.OFFLINE
        )
        val c6 = Charger(
            id = 6,
            stationOwnerId = 2,
            chargerName = "S2C2",
            chargerType = ChargerType.DC,
            powerOutput = PowerOutput.KW_22,
            connectorType = ConnectorType.TYPE_2,
            chargerStatus = ChargerStatus.AVAILABLE
        )
        val c7 = Charger(
            id = 7,
            stationOwnerId = 2,
            chargerName = "S2C3",
            chargerType = ChargerType.AC,
            powerOutput = PowerOutput.KW_50,
            connectorType = ConnectorType.CCS,
            chargerStatus = ChargerStatus.OCCUPIED
        )
        val c8 = Charger(
            id = 8,
            stationOwnerId = 2,
            chargerName = "S2C4",
            chargerType = ChargerType.DC,
            powerOutput = PowerOutput.KW_22,
            connectorType = ConnectorType.CCS,
            chargerStatus = ChargerStatus.AVAILABLE
        )

        val station1 = Station(
            id = 1,
            name = "Bornova Charge",
            latitude = 34.34,
            longitude = 35.35,
            address = "Bornova"
        )

        val station2 = Station(
            id = 2,
            name = "Karşıyaka Charge",
            latitude = 37.37,
            longitude = 38.38,
            address = "Karşıyaka"
        )

        viewModelScope.launch {
            //repo.createStation(station1)
            //repo.createStation(station2)
            //repo.createCharger(c1)
            //repo.createCharger(c2)
            //repo.createCharger(c3)
            //repo.createCharger(c4)
            //repo.createCharger(c5)
            //repo.createCharger(c6)
            //repo.createCharger(c7)
            //repo.createCharger(c8)

        }
    }

    val stations = repo.getStationDomainModels()
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            emptyList()
        )
}

