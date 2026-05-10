package com.example.fse_project.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fse_project.data.datastore.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val sessionManager: SessionManager
) : ViewModel(){

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState = _authState.asStateFlow()

    private val _isUserAdmin = MutableStateFlow<Boolean>(false)
    val isUserAdmin = _isUserAdmin.asStateFlow()

    private val _isUserOperator = MutableStateFlow<Boolean>(false)
    val isUserOperator = _isUserOperator.asStateFlow()

    init {
        viewModelScope.launch {
            sessionManager.currentUserId.collect{ userId ->
                _isUserAdmin.value = userId == 1L
                _isUserOperator.value = userId == 2L
                _authState.value = if (userId != null){
                    AuthState.LoggedIn(userId)
                }else{
                    AuthState.LoggedOut
                }
            }
        }
    }

}