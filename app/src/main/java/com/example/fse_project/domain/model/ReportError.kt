package com.example.fse_project.domain.model

import com.example.fse_project.data.local.database.entities.Report

data class ReportError(
    val resId : Long,
    val userId : Long,
    val stationId : Long,
    val chargerId : Long,
    val report : Report,
    val description : String
)
