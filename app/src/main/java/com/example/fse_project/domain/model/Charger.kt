package com.example.fse_project.domain.model

import com.example.fse_project.data.local.database.entity.ChargerStatus
import com.example.fse_project.data.local.database.entity.ChargerType
import com.example.fse_project.data.local.database.entity.ConnectorType
import com.example.fse_project.data.local.database.entity.PowerOutput

data class Charger(
    val id : Long,
    val stationOwnerId : Long, //station
    val chargerName : String,
    val chargerType: ChargerType,
    val powerOutput: PowerOutput,
    val connectorType: ConnectorType,
    val chargerStatus: ChargerStatus
)