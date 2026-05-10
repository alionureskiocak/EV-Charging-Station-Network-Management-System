package com.example.fse_project.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fse_project.data.datastore.SessionManager
import com.example.fse_project.data.local.database.entities.ChargerStatus
import com.example.fse_project.data.local.database.entities.ReservationStatus
import com.example.fse_project.domain.model.Charger
import com.example.fse_project.domain.model.ReportError
import com.example.fse_project.domain.model.Reservation
import com.example.fse_project.domain.model.Station
import com.example.fse_project.domain.repository.DirectionsRepository
import com.example.fse_project.domain.repository.ReportRepository
import com.example.fse_project.domain.repository.ReservationRepository
import com.example.fse_project.domain.repository.StationRepository
import com.example.fse_project.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val userRepo: UserRepository,
    private val stationRepo: StationRepository,
    private val reservationRepo: ReservationRepository,
    private val sessionManager: SessionManager,
    private val directionsRepo: DirectionsRepository,
    private val reportRepo: ReportRepository
) : ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    init {
        observeReservationData()
        observeStationData()
        observeAllReports()
    }

    private fun observeStationData() {
        viewModelScope.launch {
            stationRepo.getStations().collect { stations ->
                _state.update { it.copy(allStations = stations) }
            }
        }
    }

    private fun observeAllReports(){
        viewModelScope.launch {
            reportRepo.getAllReports().collect { reports ->
                _state.update { it.copy(reports = reports) }
            }
        }
    }

    private fun observeReservationData() {
        viewModelScope.launch {
            reservationRepo.getAllReservations().collect { allReservations ->
                val activeAndCompleted = allReservations.filter { it.status == ReservationStatus.COMPLETED || it.status == ReservationStatus.ACTIVE }
                val completedReservations = allReservations.filter { it.status == ReservationStatus.COMPLETED }
                val currentReservations = allReservations.filter { it.status == ReservationStatus.ACTIVE }

                val stationsAndPeakHours = activeAndCompleted
                    .groupBy { it.station }
                    .map { (station, reservations) ->
                        StationPeakHours(
                            station = station,
                            peakHours = calculatePeakHours(reservations)
                        )
                    }

                val stationsAndRevenues = completedReservations
                    .groupBy { it.station }
                    .map { ( station , reservations) ->
                        StationRevenue(
                            station = station,
                            revenue = calculateStationsRevenue(reservations)
                        )
                    }

                val peakMap = calculatePeakHours(activeAndCompleted)

                val currentDensity = calculatePeakHours(currentReservations)



                _state.update {
                    it.copy(
                        completedReservations = completedReservations,
                        peakHoursList = peakMap,
                        currentDensity = currentDensity,
                        stationsAndPeakHoursList = stationsAndPeakHours,
                        stationsAndRevenuesList = stationsAndRevenues,
                        dailyRevenue = filterLastXDays(completedReservations, 1),
                        weeklyRevenue = filterLastXDays(completedReservations, 7),
                        monthlyRevenue = filterLastXDays(completedReservations, 30),
                        annualRevenue = filterLastXDays(completedReservations, 365),
                    )
                }
            }
        }
    }

    fun logOut() {
        viewModelScope.launch {
            sessionManager.logOut()
        }
    }

    private fun calculatePeakHours(reservations: List<Reservation>): Map<Int, Int> {
        val map = mutableMapOf<Int, Int>()
        reservations.forEach { res ->
            val start = res.startTime.hour
            val end = res.endTime.hour

            for (hour in start..end) {
                map[hour] = (map[hour] ?: 0) + 1
            }
        }
        return map
    }

    private fun calculateStationsRevenue(reservations : List<Reservation>) : Double {

        return reservations.sumOf {
            it.pricePerKwh*it.actualKwh
        }
    }

    private fun filterLastXDays(reservations: List<Reservation>, day: Long): Double {
        val xDaysAgo = LocalDateTime.now().minusDays(day)
        val filteredReservations = reservations.filter {
            it.startTime.isAfter(xDaysAgo)
        }
        val totalRevenue = filteredReservations.sumOf {
            it.totalAmount
        }
        return totalRevenue
    }

    fun takeStationOffline(station: Station){
        viewModelScope.launch {
            station.chargers.forEach {
                stationRepo.updateChargerStatus(it.id, ChargerStatus.OFFLINE)
            }
        }
    }

    fun takeStationOnline(station: Station){
        viewModelScope.launch {
            station.chargers.forEach {
                stationRepo.updateChargerStatus(it.id, ChargerStatus.AVAILABLE)
            }
        }
    }

    fun takeChargerOffline(charger : Charger){
        viewModelScope.launch {
            stationRepo.updateChargerStatus(charger.id, ChargerStatus.OFFLINE)
        }
    }
}

data class UiState(
    val allStations : List<Station> = emptyList(),
    val completedReservations: List<Reservation> = emptyList(),
    val peakHoursList: Map<Int, Int> = emptyMap(),
    val currentDensity : Map<Int, Int> = emptyMap(),
    val stationsAndRevenuesList: List<StationRevenue> = emptyList(),
    val stationsAndPeakHoursList : List<StationPeakHours> = emptyList(),
    val dailyRevenue: Double = 0.0,
    val weeklyRevenue: Double = 0.0,
    val monthlyRevenue: Double = 0.0,
    val annualRevenue: Double = 0.0,

    val reports : List<ReportError> = emptyList(),
    )