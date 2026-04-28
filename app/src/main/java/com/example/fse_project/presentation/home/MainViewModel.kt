package com.example.fse_project.presentation.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fse_project.data.datastore.SessionManager
import com.example.fse_project.data.local.database.entities.ChargerStatus
import com.example.fse_project.data.local.database.entities.ReservationStatus
import com.example.fse_project.data.remote.model.Step
import com.example.fse_project.domain.model.Charger
import com.example.fse_project.domain.model.Reservation
import com.example.fse_project.domain.model.Station
import com.example.fse_project.domain.model.User
import com.example.fse_project.domain.model.Vehicle
import com.example.fse_project.domain.repository.DirectionsRepository
import com.example.fse_project.domain.repository.ReservationRepository
import com.example.fse_project.domain.repository.StationRepository
import com.example.fse_project.domain.repository.UserRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.S)
@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepo: UserRepository,
    private val stationRepo: StationRepository,
    private val reservationRepo: ReservationRepository,
    private val sessionManager: SessionManager,
    private val directionsRepo: DirectionsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(
        UiState(
            currentUser = null, currentStation = null, currentReservation = null
        )
    )
    val state = _state.asStateFlow()

    init {
        observeUserData()
        getUsers()
        observeStationsWithReservations()
        observeAllReservations()
        startBilling()
    }

    private fun observeAllReservations() {
        viewModelScope.launch {
            reservationRepo.getAllReservations().collect { reservations ->
                _state.update { it.copy(allReservations = reservations) }
            }
        }
    }

    fun onStationSearch(searchText : String){
        if (searchText.isBlank()){
            _state.update { it.copy(searchStations = _state.value.allStations) }
        }else{
            _state.update { it.copy(
                searchStations = _state.value.allStations.filter { it.name.contains(searchText, ignoreCase = true) }
            ) }
        }

    }

    private fun observeStationsWithReservations() {
        viewModelScope.launch {
            combine(
                stationRepo.getStations(),
                reservationRepo.getAllReservations()
            ) { stations, reservations ->

                stations.map { station ->
                    val updatedChargers = station.chargers.map { charger ->

                        val chargerReservations = reservations.filter {
                            it.charger.id == charger.id &&
                                    it.status in listOf(
                                ReservationStatus.ACTIVE,
                                ReservationStatus.AVAILABLE
                            )
                        }

                        val now = LocalDateTime.now()

                        val isOccupiedNow = chargerReservations.any {
                            !now.isBefore(it.startTime) && now.isBefore(it.endTime)
                        }

                        // Şu andan endOfTomorrow'a kadar her saati kontrol et
                        val endOfWindow = now.toLocalDate().plusDays(1).atTime(23, 0)

                        var checkTime = now
                        var hasFuture = false

                        while (checkTime.isBefore(endOfWindow)) {
                            val slotEnd = checkTime.plusHours(1)
                            val isSlotOccupied = chargerReservations.any { res ->
                                checkTime.isBefore(res.endTime) && res.startTime.isBefore(slotEnd)
                            }
                            if (!isSlotOccupied) {
                                hasFuture = true
                                break
                            }
                            checkTime = checkTime.plusHours(1)
                        }

                        // BURASI DEĞİŞTİ
                        val newStatus = when {
                            isOccupiedNow && hasFuture -> ChargerStatus.OCCUPIED
                            isOccupiedNow && !hasFuture -> ChargerStatus.FULL
                            // Şu an boş ama ileride rezervasyon var
                            else -> ChargerStatus.AVAILABLE
                        }

                        charger.copy(chargerStatus = newStatus)
                    }

                    station.copy(chargers = updatedChargers)
                }
            }.collect { updatedStations ->
                _state.update { currentState ->
                    // BUG FIX 2: currentStation da güncel station listesinden yenileniyor
                    // Böylece setCurrentStation'dan gelen stale data sorunu çözülüyor
                    val refreshedCurrentStation = currentState.currentStation?.let { cs ->
                        updatedStations.find { it.id == cs.id }
                    }
                    currentState.copy(
                        allStations = updatedStations,
                        searchStations = updatedStations,
                        currentStation = refreshedCurrentStation ?: currentState.currentStation
                    )
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeUserData() {
        viewModelScope.launch {

            sessionManager.currentUserId
                .filterNotNull()
                .flatMapLatest { userId ->

                    val userFlow = userRepo.getUserProfile(userId)

                    val reservationFlow =
                        reservationRepo.getAllReservationsByUserId(userId)

                    val vehicleFlow =
                        userRepo.getVehiclesByUserId(userId)


                    combine(
                        userFlow,
                        reservationFlow,
                        vehicleFlow
                    ) { user, reservations, vehicles ->

                        UiState(
                            currentUser = user,
                            usersReservations = reservations,
                            usersVehicles = vehicles,
                            currentReservation = reservations.firstOrNull { it.status == ReservationStatus.ACTIVE },
                            currentCharger = reservations.lastOrNull()?.charger,
                            currentStation =  reservations.lastOrNull()?.station,
                            currentVehicle =  reservations.lastOrNull()?.vehicle
                        )
                    }
                }
                .collect { newState ->
                    _state.update { old ->
                        var diff = 0L
                        if (newState.currentReservation != null) {
                            val res = newState.currentReservation
                            val today = LocalDateTime.now()
                            diff = Duration.between(res.startTime, today).toSeconds()
                                .toLong()
                        }
                        //if (newState.usersReservations.isNotEmpty() && findDiff(newState.currentReservation!!.startTime) >= 0) startBilling()
                       val updatedState = old.copy(
                           currentUser = newState.currentUser,
                            usersReservations = newState.usersReservations,
                            usersVehicles = newState.usersVehicles,
                            currentReservation = newState.currentReservation,
                            currentStation = newState.currentStation ?: old.currentStation,
                            currentCharger = newState.currentCharger ?: old.currentCharger,
                            currentVehicle = newState.currentVehicle ?: old.currentVehicle,
                            )

                        val res = newState.currentReservation
                        val now = LocalDateTime.now()
                        if (res != null &&
                            res.status == ReservationStatus.ACTIVE &&
                            now.isAfter(res.startTime) &&
                            now.isBefore(res.endTime)
                        ) {
                            startBilling()
                        }


                        updatedState
                    }
                }
        }
    }

    fun completeReservation(){
        val reservation = _state.value.currentReservation!!
        val user = _state.value.currentUser!!
        //val station = _state.value.currentStation!!
        val charger = _state.value.currentCharger!!
        viewModelScope.launch {
            reservation.status = ReservationStatus.COMPLETED
            val newAmount = user.wallet.balance - _timerFlow.value/60*reservation.pricePerKwh
            userRepo.updateWallet(user.id,newAmount)
            reservationRepo.updateReservationStatus(reservation.id, ReservationStatus.COMPLETED)
            stationRepo.updateChargerStatus(charger.id, ChargerStatus.AVAILABLE)
            _state.value = _state.value.copy(currentReservation = null)
        }
        stopBilling()
    }

    fun getUsers() {
        viewModelScope.launch {
            userRepo.getUsers().collect {
                _state.update { state -> state.copy(allUsers = it) }
            }
        }
    }


    fun getUsersReservations(id: Long) {
        viewModelScope.launch {
            reservationRepo.getAllReservationsByUserId(id).collect {
                _state.update { state -> state.copy(usersReservations = it) }
            }
        }
    }

    fun getUsersCars(id: Long) {
        viewModelScope.launch {
            userRepo.getVehiclesByUserId(id).collect {
                _state.update { state -> state.copy(usersVehicles = it) }
            }
        }
    }

    fun addVehicle(vehicle: Vehicle) {
        viewModelScope.launch {
            val id = userRepo.createVehicle(vehicle)
            setCurrentVehicle(vehicle.copy(id = id))
        }
    }

    fun setCurrentVehicle(vehicle: Vehicle) {
        _state.update { it.copy(currentVehicle = vehicle) }
    }


    fun setCurrentStation(stationId: Long) {
        val station = _state.value.allStations.find { it.id == stationId }
        if (station != null) {
            _state.update { it.copy(currentStation = station) }
        } else {
            // Fallback: listede yoksa repo'dan çek
            viewModelScope.launch {
                val fromRepo = stationRepo.getStationById(stationId)
                _state.update { it.copy(currentStation = fromRepo) }
            }
        }
    }

    fun setCurrentCharger(chargerId: Long) {
        // BUG FIX 3: Önce currentStation'daki charger'lara bak, repo çağrısını azalt
        val chargerFromState = _state.value.currentStation?.chargers?.find { it.id == chargerId }
        if (chargerFromState != null) {
            _state.update { it.copy(currentCharger = chargerFromState) }
        } else {
            viewModelScope.launch {
                val charger = stationRepo.getChargerById(chargerId)
                _state.update { it.copy(currentCharger = charger) }
            }
        }
    }

    fun deleteReservation(resId: Long) {
        viewModelScope.launch {
            //reservationRepo.deleteReservation(resId)
            reservationRepo.updateReservationStatus(resId, ReservationStatus.CANCELLED)
            clearRoute()
            stopBilling()
            _state.update { it.copy(currentReservation = null) }
        }

    }

    fun clearSelectedTimes() {
        _state.update { it.copy(selectedStartIndex = null, selectedEndIndex = null) }
    }

    fun selectTimeSlot(timeSlotIndex: Int) {
        val slots = _state.value.timeSlots
        val clickedSlot = slots.find { it.index == timeSlotIndex } ?: return
        if (!clickedSlot.isAvailable) return

        val currentStart = _state.value.selectedStartIndex
        val currentEnd = _state.value.selectedEndIndex

        // Yeni seçim başlatma
        if (currentStart == null || (currentStart != currentEnd) || timeSlotIndex < currentStart) {
            _state.update {
                it.copy(
                    selectedStartIndex = timeSlotIndex,
                    selectedEndIndex = timeSlotIndex
                )
            }
            return
        }

        // Seçimi iptal etme
        if (currentStart == timeSlotIndex) {
            _state.update { it.copy(selectedStartIndex = null, selectedEndIndex = null) }
            return
        }

        // Aralığı uzatma (max 4 saat, arada dolu slot yok)
        if (timeSlotIndex > currentStart) {
            val range = slots.filter { it.index in currentStart..timeSlotIndex }
            val isRangeClear = range.all { it.isAvailable }
            val duration = (timeSlotIndex - currentStart) + 1

            if (isRangeClear && duration <= 4) {
                _state.update { it.copy(selectedEndIndex = timeSlotIndex) }
            } else {
                _state.update {
                    it.copy(
                        selectedStartIndex = timeSlotIndex,
                        selectedEndIndex = timeSlotIndex
                    )
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun createReservation() {
        val startIdx = _state.value.selectedStartIndex ?: return
        val endIdx = _state.value.selectedEndIndex ?: return

        val startSlot = _state.value.timeSlots.find { it.index == startIdx }
        val endSlot = _state.value.timeSlots.find { it.index == endIdx }

        val now = LocalDate.now()
        if (startSlot != null && endSlot != null) {
            val startTime = LocalDateTime.of(startSlot.date, LocalTime.of(startSlot.hour, 0))
            val endTime = LocalDateTime.of(endSlot.date, LocalTime.of(endSlot.hour, 0)).plusHours(1)

            viewModelScope.launch {
                val reservation = Reservation(
                    id = 0,
                    user = _state.value.currentUser!!,
                    vehicle = _state.value.currentVehicle!!,
                    station = _state.value.currentStation!!,
                    charger = _state.value.currentCharger!!,
                    startTime = startTime,
                    endTime = endTime,
                    pricePerKwh = 4.0,
                    status = ReservationStatus.ACTIVE
                )
                val id = reservationRepo.createReservation(reservation)
                clearSelectedTimes()

                _state.value.currentUser?.let { getUsersReservations(it.id) }
                val station = _state.value.currentStation!!

                val userLocation = _state.value.userLocation
                userLocation?.let {
                    fetchDirections(
                        originLat = userLocation.latitude,
                        originLng = userLocation.longitude,
                        destLat = station.latitude,
                        destLng = station.longitude
                    )
                }
                _state.update { it.copy(currentReservation = reservation.copy(id = id)) }

            }
        }


    }

    private var job: Job? = null

    private val _timerFlow = MutableStateFlow(0)
    val timerFlow = _timerFlow.asStateFlow()

    fun startBilling() {

        if (job?.isActive == true) return
        job = viewModelScope.launch {
            while (true) {
                delay(1000)
                val res = _state.value.currentReservation ?: continue
                val startTime = res.startTime
                val now = LocalDateTime.now()


                if (now.isBefore(startTime)) {
                    _timerFlow.value = 0
                    continue
                }

                if (now.isAfter(res.endTime)) {
                   completeReservation()
                    continue
                }
                //TODO AYNI SAAT İÇİNDE GEÇ REZERVASYON YAPILIRSA SAAT BAŞINDAN İTİBAREN PARAYI ÇEKİYOR
                val diff = Duration.between(startTime, now).toSeconds()
                _timerFlow.value = diff.toInt()
                _state.update { it.copy(isChargingNow = true) }
            }
        }
    }

    fun stopBilling() {
        job?.cancel()
        _state.update { it.copy(isChargingNow = false) }
        _timerFlow.value = 0
    }

    fun getReservationTimeSlots(chargerId: Long) {
        val referenceDate: LocalDateTime = LocalDateTime.now()
        val today = referenceDate.toLocalDate()
        val tomorrow = today.plusDays(1)

        // BUG FIX 1: allReservations artık dolu (observeAllReservations sayesinde)
        val activeReservationsForCharger = _state.value.allReservations.filter {
            it.charger.id == chargerId &&
                    it.status in listOf(ReservationStatus.AVAILABLE, ReservationStatus.ACTIVE)
        }

        val slots = mutableListOf<TimeSlot>()
        var globalIndex = 1

        // Bugünün slotları
        for (hour in referenceDate.hour..23) {
            val slotStart = LocalDateTime.of(today, LocalTime.of(hour, 0))
            val slotEnd = slotStart.plusHours(1)
            val now = LocalDateTime.now()
            val isReserved = activeReservationsForCharger.any { res ->
                slotStart.isBefore(res.endTime) && res.startTime.isBefore(slotEnd)
            }

            slots.add(
                TimeSlot(
                    index = globalIndex++,
                    hour = if (hour == now.hour) now.hour else hour,
                    date = today,
                    timeLabel = String.format(
                        "%02d:00 - %02d:00",
                        hour,
                        if (hour == 23) 0 else hour + 1
                    ),
                    isAvailable = !isReserved
                )
            )
        }

        // Yarının slotları (toplam 24'e tamamla)
        val remainingSlotCount = 24 - slots.size
        for (hour in 0 until remainingSlotCount) {
            val slotStart = LocalDateTime.of(tomorrow, LocalTime.of(hour, 0))
            val slotEnd = slotStart.plusHours(1)

            val isOccupied = activeReservationsForCharger.any { res ->
                slotStart.isBefore(res.endTime) && res.startTime.isBefore(slotEnd)
            }

            slots.add(
                TimeSlot(
                    index = globalIndex++,
                    hour = hour,
                    date = tomorrow,
                    timeLabel = String.format("%02d:00 - %02d:00", hour, hour + 1),
                    isAvailable = !isOccupied
                )
            )
        }

        _state.update { it.copy(timeSlots = slots) }
    }

    fun logOut() {
        viewModelScope.launch {
            sessionManager.logOut()
        }
    }

    fun fetchDirections(
        originLat: Double,
        originLng: Double,
        destLat: Double,
        destLng: Double
    ) {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingRoute = true, routeError = null) }

            directionsRepo.getDirections(
                originLat = originLat,
                originLng = originLng,
                destLat = destLat,
                destLng = destLng
            ).fold(
                onSuccess = { response ->
                    val route = response.routes.firstOrNull()
                    val leg = route?.legs?.firstOrNull()

                    _state.update {
                        it.copy(
                            isLoadingRoute = false,
                            routePolyline = route?.overview_polyline?.points,
                            routeDistance = leg?.distance?.text,
                            routeDuration = leg?.duration?.text,
                            routeSteps = leg?.steps ?: emptyList()
                        )
                    }
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            isLoadingRoute = false,
                            routeError = error.message
                        )
                    }
                }
            )
        }
    }

    fun setUserLocation(lat: Double, lng: Double) {
        _state.update {
            it.copy(
                userLocation = LatLng(lat, lng)
            )
        }
    }

    fun clearRoute() {
        _state.update {
            it.copy(
                routePolyline = null,
                routeDistance = null,
                routeDuration = null,
                routeSteps = emptyList(),
                routeError = null
            )
        }
    }

    fun updateWallet(balance: Double) {
        viewModelScope.launch {
            _state.value.currentUser?.let {
                val id = _state.value.currentUser!!.id
                userRepo.updateWallet(id, balance)
            }

        }
    }

    fun changeCancelDialogStatus() {
        _state.update { it.copy(showResCancelDialog = !_state.value.showResCancelDialog) }
    }
}

data class UiState(
    val allUsers: List<User> = emptyList(),
    val allStations: List<Station> = emptyList(),
    val searchStations : List<Station> = emptyList(),
    val allReservations: List<Reservation> = emptyList(),
    val usersReservations: List<Reservation> = emptyList(),
    val timeSlots: List<TimeSlot> = emptyList(),
    val restrictedTimeSlots: List<TimeSlot> = emptyList(),
    val usersVehicles: List<Vehicle> = emptyList(),
    val chargerItems: List<ChargerItem> = emptyList(),
    val selectedStartIndex: Int? = null,
    val selectedEndIndex: Int? = null,
    val currentUser: User? = null,
    val currentVehicle: Vehicle? = null,
    val currentStation: Station? = null,
    val currentCharger: Charger? = null,
    val currentReservation: Reservation? = null,
    val firstChoice: TimeSlot? = null,
    val secondChoice: TimeSlot? = null,
    val routePolyline: String? = null,
    val routeDistance: String? = null,
    val routeDuration: String? = null,
    val routeSteps: List<Step> = emptyList(),
    val isLoadingRoute: Boolean = false,
    val routeError: String? = null,
    val showResCancelDialog: Boolean = false,

    val isChargingNow : Boolean = false,

    val userLocation: LatLng? = null
)