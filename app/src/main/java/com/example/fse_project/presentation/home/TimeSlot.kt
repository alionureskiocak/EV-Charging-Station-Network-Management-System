package com.example.fse_project.presentation.home

import java.time.LocalDate

data class TimeSlot(
    val index: Int,
    val hour: Int,
    val date: LocalDate,
    val timeLabel: String,
    val isAvailable: Boolean
)
