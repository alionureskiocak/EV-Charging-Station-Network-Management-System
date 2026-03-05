package com.example.fse_project.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.yml.charts.common.extensions.isNotNull
import com.example.fse_project.data.datastore.SessionManager
import com.example.fse_project.domain.model.User
import com.example.fse_project.domain.model.Vehicle
import com.example.fse_project.domain.model.Wallet
import com.example.fse_project.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.metadata.internal.metadata.serialization.StringTable
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel(){


    private val _state = MutableStateFlow(LoginUiState())
    val state = _state.asStateFlow()


    fun getUsers(){
        viewModelScope.launch {
            repository.getUsers().collect {
                _state.value = _state.value.copy(
                    allUsers = it
                )
            }
        }
    }

    fun signUp(
        name : String,
        email : String,
        password: String
    ){
        viewModelScope.launch {
            val user = User(
                id = -1,
                name = name,
                email = email,
                password = password
            )
            repository.createUser(user)
        }
    }

    fun signIn(email : String, password : String) {
        viewModelScope.launch {

            val user = repository.login(email,password)
            if (user.isNotNull()){
                _state.value = _state.value.copy(
                    isLoginSuccessful = true
                )
                sessionManager.setUserId(user!!.id)
            }else{
                _state.value = _state.value.copy(
                    error = "Wrong username/password"
                )
            }

        }

    }
}

data class LoginUiState(
    val allUsers : List<User> = emptyList(),
    val isLoginSuccessful : Boolean = false,
    val error : String = ""
)