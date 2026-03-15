package com.example.fse_project.presentation.home

data class TimeSlot(
    val index : Int,
    val hour : Int,
    val timeLabel : String,
    var isAvailable : Boolean
)
