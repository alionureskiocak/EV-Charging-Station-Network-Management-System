package com.example.fse_project.domain.model

import com.example.fse_project.data.local.database.entities.Report

data class ReportError(
    val id : Long,
    val userId : Long,
    val stationId : Long,
    val report : Report,
    val description : String
)
