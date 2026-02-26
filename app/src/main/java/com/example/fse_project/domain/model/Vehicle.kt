package com.example.fse_project.domain.model

import com.example.fse_project.data.local.database.entity.ConnectorType

data class Vehicle(
    val id : Int,
    val ownerId : Int, //user
    val brand : String,
    val model : String,
    val capacity : Int,
    val connectorType: ConnectorType,
    val licensePlate : String,
)
