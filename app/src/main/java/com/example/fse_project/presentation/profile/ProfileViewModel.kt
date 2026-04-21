package com.example.fse_project.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fse_project.data.datastore.SessionManager
import com.example.fse_project.domain.model.Reservation
import com.example.fse_project.domain.model.User
import com.example.fse_project.domain.repository.DirectionsRepository
import com.example.fse_project.domain.repository.ReservationRepository
import com.example.fse_project.domain.repository.StationRepository
import com.example.fse_project.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepo: UserRepository,
    private val stationRepo: StationRepository,
    private val reservationRepo: ReservationRepository,
    private val sessionManager: SessionManager,
    private val directionsRepo: DirectionsRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    init {
        getUserProfile()
    }

    fun getUserProfile() {
        viewModelScope.launch {
            sessionManager.currentUserId.filterNotNull().collectLatest { id ->
                id.let {
                    val user = userRepo.getUserProfile(it)
                    reservationRepo.getAllReservationsByUserId(id).collect { ress ->
                        _state.update { it.copy(userReservations = ress) }
                    }
                    _state.update {
                        it.copy(
                            currentUser = user
                        )
                    }
                }
            }
        }

    }

    fun changeCancelDialogStatus() {
        _state.update { it.copy(showResCancelDialog = !_state.value.showResCancelDialog) }
    }
}

data class UiState(
    val currentUser: User? = null,
    val userReservations: List<Reservation> = emptyList(),
    val showResCancelDialog: Boolean = false
)