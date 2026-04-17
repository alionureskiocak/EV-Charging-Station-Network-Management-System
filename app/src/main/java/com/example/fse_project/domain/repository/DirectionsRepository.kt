package com.example.fse_project.domain.repository

import com.example.fse_project.data.remote.model.DirectionResponse

interface DirectionsRepository {
    suspend fun getDirections(
        originLat: Double,
        originLng: Double,
        destLat: Double,
        destLng: Double
    ): Result<DirectionResponse>
}