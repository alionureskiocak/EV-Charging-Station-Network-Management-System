package com.example.fse_project

import com.example.fse_project.data.local.database.entities.PowerOutput
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.time.LocalDateTime

@RunWith(JUnit4::class)
class BillingTest {

    private lateinit var viewModel: com.example.fse_project.presentation.home.MainViewModel

    @Before
    fun setUp() {
        viewModel = TestFixtures.buildViewModel()
    }

    @Test
    fun calculateConsumedKwh_returnsCorrectKwh_forNormalSession() {
        val start = LocalDateTime.of(2026, 5, 15, 10, 0)
        val end   = LocalDateTime.of(2026, 5, 15, 11, 0) // 3600 s

        val result = viewModel.calculateConsumedKwh(
            startTime         = start,
            endTime           = end,
            powerOutput       = PowerOutput.KW_50,
            vehicleCapacity   = 75.0,
            vehicleCurrentKwh = 25.0
        )
        assertEquals(50.0, result, 0.01)
    }

    @Test
    fun calculateConsumedKwh_cappedAtRemainingCapacity_whenBatteryNearlyFull() {

        val start = LocalDateTime.of(2026, 5, 15, 10, 0)
        val end   = LocalDateTime.of(2026, 5, 15, 11, 0)
        val result = viewModel.calculateConsumedKwh(
            startTime         = start,
            endTime           = end,
            powerOutput       = PowerOutput.KW_50,
            vehicleCapacity   = 75.0,
            vehicleCurrentKwh = 65.0
        )
        assertEquals(10.0, result, 0.01)
    }
}