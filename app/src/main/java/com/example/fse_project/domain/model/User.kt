package com.example.fse_project.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Long,
    val name: String,
    val email: String,
    val password : String,
    val vehicles : List<Vehicle> = emptyList(),
    val wallet : Wallet = Wallet(0,0.0)
)
