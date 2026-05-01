package com.example.fse_project.domain.repository

import com.example.fse_project.data.local.database.entities.Report
import com.example.fse_project.data.local.database.entities.ReportErrorEntity
import com.example.fse_project.domain.model.ReportError
import kotlinx.coroutines.flow.Flow

interface ReportRepository {


    suspend fun insertReport(report : ReportError) : Long
    fun getAllReports() : Flow<List<ReportError>>
}