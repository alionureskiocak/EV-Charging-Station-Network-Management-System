package com.example.fse_project.data.remote.service

import com.example.fse_project.data.remote.model.DirectionResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface DirectionsApi {

    @GET("maps/api/directions/json")
    suspend fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") apiKey: String,
        @Query("language") language: String = "tr",
        @Query("mode") mode: String = "driving"
    ): DirectionResponse
}