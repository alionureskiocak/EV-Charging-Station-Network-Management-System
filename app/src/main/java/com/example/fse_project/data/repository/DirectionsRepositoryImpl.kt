package com.example.fse_project.data.repository

import com.example.fse_project.data.remote.model.DirectionResponse
import com.example.fse_project.data.remote.service.DirectionsApi
import com.example.fse_project.domain.repository.DirectionsRepository
import com.google.maps.android.BuildConfig
import javax.inject.Inject

class DirectionsRepositoryImpl @Inject constructor(
    private val api: DirectionsApi
) : DirectionsRepository {

    override suspend fun getDirections(
        originLat: Double,
        originLng: Double,
        destLat: Double,
        destLng: Double
    ): Result<DirectionResponse> {
        return try {
            val response = api.getDirections(
                origin = "$originLat,$originLng",
                destination = "$destLat,$destLng",
                apiKey = "AIzaSyAzsgFWAZrlyi7FvX0bQYEsK1dzWNTtXEs"
            )

            if (response.status == "OK") {
                Result.success(response)
            } else {
                Result.failure(Exception("Directions API hatası: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}