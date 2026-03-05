package com.example.fse_project.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fse_project.data.local.database.entities.ChargerStatus
import com.example.fse_project.data.local.database.entities.ChargerType
import com.example.fse_project.data.local.database.entities.ConnectorType
import com.example.fse_project.data.local.database.entities.PowerOutput
import com.example.fse_project.domain.model.Charger
import com.example.fse_project.domain.model.Station
import com.example.fse_project.domain.model.User
import com.example.fse_project.domain.model.Wallet
import com.example.fse_project.domain.repository.ReservationRepository
import com.example.fse_project.domain.repository.StationRepository
import com.example.fse_project.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepo: UserRepository,
    private val stationRepo : StationRepository,
    private val reservationRepo : ReservationRepository
) : ViewModel() {

    init {
        //denemeVerileriniEkle()
    }

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    fun getUserProfile(){
        viewModelScope.launch {
            val user = userRepo.getUserProfile()
        }
    }





    fun denemeVerileriniEkle() {
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
            repository.createStation(station1)
            repository.createStation(station2)
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
}

data class UiState(
    val currentUser : User = User(
        id = -1,
        name = "",
        email = "",
        vehicles = emptyList(),
        wallet = Wallet(userId = -1, balance = 0.0)
    ),
    val currentStation : Station = Station(
        id = -1,
        name = "",
        latitude = 0.0,
        longitude = 0.0,
        address = "",
        chargers = emptyList()
    )
)

