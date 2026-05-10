package com.example.fse_project.util

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Long.toFormattedDate(): String {
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")

    return Instant
        .ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()
        .format(formatter)
}