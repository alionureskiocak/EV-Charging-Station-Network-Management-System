package com.example.fse_project.presentation.admin

import com.example.fse_project.domain.model.Station

data class StationPeakHours(
    val station: Station = Station(
        id = -1,
        name = "",
        latitude = 0.0,
        longitude = 0.0,
        address = "",
        chargers = emptyList()
    ),
    val peakHours: Map<Int, Int> = emptyMap()
)