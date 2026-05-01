package com.example.fse_project.data.repository

import com.example.fse_project.data.local.database.AppDao
import com.example.fse_project.data.local.database.entities.Report
import com.example.fse_project.data.local.database.entities.ReportErrorEntity
import com.example.fse_project.data.mapper.toDomain
import com.example.fse_project.domain.model.ReportError
import com.example.fse_project.domain.repository.ReportRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportRepositoryImpl @Inject constructor(
    private val dao : AppDao
) : ReportRepository{
    override suspend fun insertReport(report: ReportError): Long {
        return dao.insertReport(report.toDomain())
    }

    override fun getAllReports(): Flow<List<ReportError>> {
        return dao.getAllReports().map { it.map { it.toDomain() } }
    }
}