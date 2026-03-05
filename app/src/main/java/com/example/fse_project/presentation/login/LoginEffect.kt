package com.example.fse_project.presentation.login

sealed interface LoginEffect {

    data class ShowToast(val message : String) : LoginEffect
}