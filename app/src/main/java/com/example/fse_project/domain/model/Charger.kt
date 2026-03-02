package com.example.fse_project.domain.model

import com.example.fse_project.data.local.database.entities.ChargerStatus
import com.example.fse_project.data.local.database.entities.ChargerType
import com.example.fse_project.data.local.database.entities.ConnectorType
import com.example.fse_project.data.local.database.entities.PowerOutput

data class Charger(
    val id : Long,
    val stationOwnerId : Long, //station
    val chargerName : String,
    val chargerType: ChargerType,
    val powerOutput: PowerOutput,
    val connectorType: ConnectorType,
    val chargerStatus: ChargerStatus
)