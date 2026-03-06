package com.example.fse_project.presentation.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fse_project.data.datastore.SessionManager
import com.example.fse_project.data.local.database.entities.ReservationStatus
import com.example.fse_project.domain.model.Charger
import com.example.fse_project.domain.model.Reservation
import com.example.fse_project.domain.model.Station
import com.example.fse_project.domain.model.User
import com.example.fse_project.domain.model.Vehicle
import com.example.fse_project.domain.repository.ReservationRepository
import com.example.fse_project.domain.repository.StationRepository
import com.example.fse_project.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepo: UserRepository,
    private val stationRepo: StationRepository,
    private val reservationRepo: ReservationRepository,
    private val sessionManager: SessionManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    init {
        getUserProfile()
        getUsers()
        getAllReservations()
        getAllStations()
    }

    private val _state = MutableStateFlow(UiState(
        currentUser = null,
        currentStation = null,
        currentReservation = null
    ))
    val state = _state.asStateFlow()


    fun getUsers(){
        viewModelScope.launch {
            userRepo.getUsers().collect {
                _state.value = _state.value.copy(
                    allUsers = it
                )
            }
        }
    }

    fun getAllReservations(){
        viewModelScope.launch {
            reservationRepo.getAllReservations().collect {
                _state.value = _state.value.copy(
                    allReservations = it
                )
            }
        }
    }

    fun getAllStations(){
        viewModelScope.launch {
            stationRepo.getStations().collect {
                _state.value = _state.value.copy(
                    allStations = it
                )
            }
        }
    }

    fun getUsersReservations(id : Long) {
        viewModelScope.launch {
            reservationRepo.getAllReservationsByUserId(id).collect {
                _state.value = _state.value.copy(
                    usersReservations = it
                )
            }
        }
    }


    fun getUserProfile() {
        viewModelScope.launch {
            sessionManager.currentUserId
                .filterNotNull()
                .collectLatest { userId ->
                    val user = userRepo.getUserProfile(userId)
                    if (user != null) {
                        _state.value = _state.value.copy(
                            currentUser = user
                        )
                        getUsersReservations(user.id)
                    } else {
                        println("Kullanıcı veritabanında bulunamadı!")
                    }
                }
        }
    }

    fun getChargerById(chargerId : Long){
        viewModelScope.launch {
           _state.value = _state.value.copy(
               currentCharger = stationRepo.getChargerById(chargerId)
           )
        }
    }

    fun getChargersForStation(stationId : Long) : List<ChargerItem>{

        //vehicle ve station alındıysa çağır
        val currentStation = _state.value.allStations.find { it.id == stationId }
        val currentVehicle = _state.value.currentVehicle

        currentStation?.let {
            currentVehicle?.let {

                return currentStation.chargers.map { charger ->
                    val clickable = charger.connectorType == currentVehicle.connectorType
                    ChargerItem(charger = charger,clickable = clickable)
            }
        }
        }

        return emptyList()
    }

    fun getReservationTimeSlots(chargerId : Long, selectedDate : LocalDateTime = LocalDateTime.now()) : List<TimeSlot>{

        val targetDates = listOf(
            selectedDate.toLocalDate(),
            selectedDate.toLocalDate().plusDays(1)
        )

        val allReservations = _state.value.allReservations.filter {
            it.charger.id == chargerId &&
                    it.status in listOf(ReservationStatus.AVAILABLE, ReservationStatus.ACTIVE) &&
                    it.startTime.toLocalDate() in targetDates
        }

        val slots = mutableListOf<TimeSlot>()
        val startHour = selectedDate.hour

            for (hour in startHour..23){
            val isOccupied = allReservations.any{ reservation ->
                val startTime = reservation.startTime
                val endTime = reservation.endTime
                val time = LocalDateTime.of(selectedDate.toLocalDate(), LocalTime.of(hour, 0))
                time.isBefore(endTime) && !time.isBefore(startTime)
            }
            slots.add(
                TimeSlot(
                    hour = hour,
                    timeLabel = String.format("%02d:00", hour),
                    isAvailable = !isOccupied
                )
            )
        }

        for (hour in 0 until startHour){
            val isOccupied = allReservations.any{ reservation ->
                val startTime = reservation.startTime
                val endTime = reservation.endTime

                val time = LocalDateTime.of(selectedDate.toLocalDate().plusDays(1), LocalTime.of(hour, 0))
                time.isBefore(endTime) && !time.isBefore(startTime)
            }
            slots.add(
                TimeSlot(
                    hour = hour,
                    timeLabel = String.format("%02d:00", hour),
                    isAvailable = !isOccupied
                )
            )
        }

        return slots
    }
}

data class UiState(
    val allUsers : List<User> = emptyList(),
    val allStations : List<Station> = emptyList(),
    val allReservations : List<Reservation> = emptyList(),
    val usersReservations: List<Reservation> = emptyList(),
    val currentUser: User? = null,
    val currentVehicle : Vehicle? = null,
    val currentStation: Station? = null,
    val currentCharger : Charger? = null,
    val currentReservation: Reservation? = null
)

