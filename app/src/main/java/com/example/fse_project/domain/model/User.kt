package com.example.fse_project.domain.model

data class User(
    val id: Long,
    val name: String,
    val email: String,
    val password : String,
    val vehicles : List<Vehicle>,
    val wallet : Wallet
)
