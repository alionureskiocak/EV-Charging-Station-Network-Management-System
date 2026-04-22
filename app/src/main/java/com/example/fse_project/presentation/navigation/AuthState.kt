package com.example.fse_project.presentation.navigation

sealed class AuthState {

    object Loading : AuthState()
    object LoggedOut : AuthState()
    data class LoggedIn(val userId : Long) : AuthState()
}