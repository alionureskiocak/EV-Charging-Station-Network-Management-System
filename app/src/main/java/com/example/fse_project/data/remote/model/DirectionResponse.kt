package com.example.fse_project.data.remote.model

data class DirectionResponse(
    val geocoded_waypoints: List<GeocodedWaypoint>,
    val routes: List<Route>,
    val status: String
)