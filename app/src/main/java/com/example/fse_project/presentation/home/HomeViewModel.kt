package com.example.fse_project.presentation.home

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fse_project.data.datastore.SessionManager
import com.example.fse_project.data.local.database.entities.ChargerStatus
import com.example.fse_project.data.local.database.entities.ReservationStatus
import com.example.fse_project.domain.model.Charger
import com.example.fse_project.domain.model.Reservation
import com.example.fse_project.domain.model.Station
import com.example.fse_project.domain.model.StationStatus
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
import kotlin.math.abs

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

    private val _state = MutableStateFlow(
        UiState(
            currentUser = null, currentStation = null, currentReservation = null
        )
    )
    val state = _state.asStateFlow()


    fun getUsers() {
        viewModelScope.launch {
            userRepo.getUsers().collect {
                _state.value = _state.value.copy(
                    allUsers = it
                )
            }
        }
    }

    fun getAllReservations() {
        viewModelScope.launch {
            reservationRepo.getAllReservations().collect {
                _state.value = _state.value.copy(
                    allReservations = it
                )
            }
        }
    }

    fun getAllStations() {
        viewModelScope.launch {
            stationRepo.getStations().collect {
                _state.value = _state.value.copy(
                    allStations = it
                )
            }
        }
    }

    fun getUsersReservations(id: Long) {
        viewModelScope.launch {
            reservationRepo.getAllReservationsByUserId(id).collect {
                _state.value = _state.value.copy(
                    usersReservations = it
                )
            }
        }
    }

    fun getUsersCars(id: Long) {
        viewModelScope.launch {
            userRepo.getVehiclesByUserId(id).collect {
                _state.value = _state.value.copy(
                    usersVehicles = it
                )
            }
        }
    }

    fun getUserProfile() {
        viewModelScope.launch {
            sessionManager.currentUserId.filterNotNull().collectLatest { userId ->
                val user = userRepo.getUserProfile(userId)
                if (user != null) {
                    _state.value = _state.value.copy(
                        currentUser = user
                    )
                    getUsersReservations(user.id)
                    getUsersCars(user.id)
                } else {
                    println("Kullanıcı veritabanında bulunamadı!")
                }
            }
        }
    }

    fun getChargerById(chargerId: Long) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                currentCharger = stationRepo.getChargerById(chargerId)
            )
        }
    }

    fun getChargersForStation(stationId: Long): List<ChargerItem> {

        val currentStation = _state.value.allStations.find { it.id == stationId }
        val currentVehicle = _state.value.currentVehicle

        currentStation?.let {
            currentVehicle?.let {
                println("current station: ${currentStation.id}")
                // ViewModel veya UI State Mapper Güncellemesi
                val items = currentStation.chargers.map { charger ->
                    val clickable = charger.connectorType == currentVehicle.connectorType &&
                            currentStation.status != StationStatus.OFFLINE &&
                            charger.chargerStatus != ChargerStatus.OFFLINE

                    val clickableText = when {
                        currentStation.status == StationStatus.OFFLINE || charger.chargerStatus == ChargerStatus.OFFLINE -> "Çevrimdışı"
                        charger.connectorType != currentVehicle.connectorType -> "Uyumsuz soket"
                        charger.chargerStatus == ChargerStatus.OCCUPIED -> "Dolu (Randevu Alınabilir)" // <-- Kullanıcıyı yönlendiren metin
                        else -> "Uygun"
                    }

                    val statusColor: Color = when {
                        clickable && charger.chargerStatus == ChargerStatus.OCCUPIED -> Color(0xFFFF9100) // Turuncu
                        !clickable -> Color(0xFFF44336) // Kırmızı
                        clickable && charger.chargerStatus == ChargerStatus.AVAILABLE -> Color(0xFF76FF03) // Yeşil
                        else -> Color.Gray
                    }

                   val chargerItem =  ChargerItem(
                        charger = charger,
                        clickable = clickable,
                        clickableText = clickableText,
                    )
                    chargerItem.statusColor = statusColor
                    chargerItem
                }
                _state.value = _state.value.copy(chargerItems = items)
                return items
            }
        }

        return emptyList()
    }

    fun addVehicle(vehicle: Vehicle) {
        viewModelScope.launch {
            val id = userRepo.createVehicle(vehicle)
            setCurrentVehicle(vehicle.copy(id = id))
        }
    }

    fun setCurrentVehicle(vehicle: Vehicle) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                currentVehicle = vehicle
            )
        }
    }

    fun clearSelectedTimes() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                selectedStartIndex = null,
                selectedEndIndex = null
            )
        }
    }

    fun createReservation(startTime: Int, endTime: Int) {
        val startTime =
            LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.of(startTime, 0))
        val endTime = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.of(endTime, 0))
        viewModelScope.launch {
            val user = _state.value.currentUser
            val vehicle = _state.value.currentVehicle
            val station = _state.value.currentStation
            val charger = _state.value.currentCharger
            println("$user\n$vehicle\n$station\n$charger\n")
            val pricePerKwh = 4.0
            if (user != null && vehicle != null && station != null && charger != null) {
                val reservation = Reservation(
                    id = 0,
                    user = user,
                    vehicle = vehicle,
                    station = station,
                    charger = charger,
                    startTime = startTime,
                    endTime = endTime,
                    pricePerKwh = pricePerKwh,
                    status = ReservationStatus.ACTIVE
                )
                reservationRepo.createReservation(reservation)
            }
        }
        println(_state.value.allReservations)
    }

    fun setCurrentStation(stationId : Long){
        viewModelScope.launch {
            val station = stationRepo.getStationById(stationId)
            _state.value = _state.value.copy(
                currentStation = station
            )
        }
    }

    fun setCurrentCharger(chargerId: Long){
        viewModelScope.launch {
            val charger = stationRepo.getChargerById(chargerId)

            _state.value = _state.value.copy(
                currentCharger = charger
            )
        }
    }

    fun selectTimeSlot(timeSlotIndex: Int) {
        val slots = _state.value.timeSlots
        val currentStart = _state.value.selectedStartIndex
        val currentEnd = _state.value.selectedEndIndex

        val clickedSlot = slots.find { it.index == timeSlotIndex } ?: return
        if (!clickedSlot.isAvailable) return // Kutu doluysa hiç tepki verme

        // 1. DURUM: Hiç seçim yoksa VEYA önceden uzun bir aralık seçilmişse (yeni bir seçime başlıyorsa)
        // VEYA tıklanan yer mevcut başlangıçtan gerideyse (Örn: 15'i seçmişti, 13'e tıkladı)
        if (currentStart == null || (currentStart != currentEnd) || timeSlotIndex < currentStart) {
            // Tıkladığı kutuyu hem başlangıç hem bitiş yap (Sadece o 1 saati seç)
            _state.value = _state.value.copy(
                selectedStartIndex = timeSlotIndex,
                selectedEndIndex = timeSlotIndex
            )
            return
        }

        // 2. DURUM: Zaten seçili olan tek kutuya tekrar tıkladı (Seçimi iptal et)
        if (currentStart == timeSlotIndex && currentEnd == timeSlotIndex) {
            _state.value = _state.value.copy(
                selectedStartIndex = null,
                selectedEndIndex = null
            )
            return
        }

        // 3. DURUM: İleriye doğru yeni bir kutu seçti (Aralığı uzatmak istiyor)
        if (timeSlotIndex > currentStart) {
            var isRangeValid = true

            // Başlangıç ile tıklanan son kutu arasındaki tüm kutuları kontrol et
            for (i in currentStart..timeSlotIndex) {
                val slotInRange = slots.find { it.index == i }
                if (slotInRange == null || !slotInRange.isAvailable) {
                    isRangeValid = false
                    break
                }
            }

            // Seçilen kutu sayısı (Örn: 3. kutudan 5. kutuya = 3 kutu = 3 saat)
            val duration = (timeSlotIndex - currentStart) + 1

            if (isRangeValid && duration <= 4) {
                // Her şey yasal! Aralığı seçilen yeni kutuya kadar uzat
                _state.value = _state.value.copy(selectedEndIndex = timeSlotIndex)
            } else {
                // Hatalı seçimse (arada dolu var veya 4 saati aştı), uzatmaya izin verme,
                // sadece tıklanan yeri yeni 1 saatlik seçim olarak kabul et.
                _state.value = _state.value.copy(
                    selectedStartIndex = timeSlotIndex,
                    selectedEndIndex = timeSlotIndex
                )
            }
        }
    }

    fun getReservationTimeSlots(
        chargerId: Long, selectedDate: LocalDateTime = LocalDateTime.now()
    ): List<TimeSlot> {

        val targetDates = listOf(
            selectedDate.toLocalDate(), selectedDate.toLocalDate().plusDays(1)
        )

        val allReservations = _state.value.allReservations.filter {
            it.charger.id == chargerId && it.status in listOf(
                ReservationStatus.AVAILABLE,
                ReservationStatus.ACTIVE
            ) && it.startTime.toLocalDate() in targetDates
        }

        val slots = mutableListOf<TimeSlot>()
        val startHour = selectedDate.hour
        var index = 0

        // 1. KISIM: Bugün için kalan saatler (Seçilen saatten gece 23:59'a kadar)
        for (hour in startHour..23) {
            index++

            // Slot'un tam başlangıç ve bitiş zamanını (1 saatlik blok olarak) oluşturuyoruz
            val slotStart = LocalDateTime.of(selectedDate.toLocalDate(), LocalTime.of(hour, 0))
            val slotEnd = slotStart.plusHours(1)

            val isOccupied = allReservations.any { reservation ->
                // Kusursuz Kesişim (Overlap) Kuralı
                slotStart.isBefore(reservation.endTime) && reservation.startTime.isBefore(slotEnd)
            }

            // Kutu üzerindeki yazıyı sezgisel hale getiriyoruz: "05:00 - 06:00"
            val endHour = if (hour == 23) 0 else hour + 1
            val label = String.format("%02d:00 - %02d:00", hour, endHour)

            slots.add(
                TimeSlot(
                    index = index,
                    hour = hour,
                    timeLabel = label,
                    isAvailable = !isOccupied
                )
            )
        }

        // 2. KISIM: Ertesi günün saatleri (Gece 00:00'dan, bugünkü başlangıç saatine kadar)
        // Böylece kullanıcıya her zaman 24 saatlik kesintisiz bir rezervasyon penceresi sunulur.
        for (hour in 0 until startHour) {
            index++

            val slotStart = LocalDateTime.of(selectedDate.toLocalDate().plusDays(1), LocalTime.of(hour, 0))
            val slotEnd = slotStart.plusHours(1)

            val isOccupied = allReservations.any { reservation ->
                slotStart.isBefore(reservation.endTime) && reservation.startTime.isBefore(slotEnd)
            }

            val endHour = if (hour == 23) 0 else hour + 1
            val label = String.format("%02d:00 - %02d:00", hour, endHour)

            slots.add(
                TimeSlot(
                    index = index,
                    hour = hour,
                    timeLabel = label,
                    isAvailable = !isOccupied
                )
            )
        }

        // UI'ı tetiklemek için State'i güncelliyoruz
        _state.value = _state.value.copy(timeSlots = slots)

        return slots
    }





}

data class UiState(
    val allUsers: List<User> = emptyList(),
    val allStations: List<Station> = emptyList(),
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
    val secondChoice: TimeSlot? = null
)

