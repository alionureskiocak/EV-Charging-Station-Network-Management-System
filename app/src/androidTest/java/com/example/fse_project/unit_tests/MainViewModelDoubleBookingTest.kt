package com.example.fse_project

import com.example.fse_project.data.local.database.entities.ReservationStatus
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.time.LocalDate
import java.time.LocalDateTime

@RunWith(JUnit4::class)
class DoubleBookingTest {

    private lateinit var viewModel: com.example.fse_project.presentation.home.MainViewModel
    private val tomorrow = LocalDate.now().plusDays(1)

    @Before
    fun setUp() {
        viewModel = TestFixtures.buildViewModel()
    }

    @Test   // TC-04a: çakışan slot → unavailable
    fun getReservationTimeSlots_slotUnavailable_whenOverlapWithActiveReservation() {
        // Arrange – charger 1 için yarın 10:00–11:00 arası aktif rezervasyon
        viewModel.setTestState(
            allReservations = listOf(
                TestFixtures.reservation(
                    chargerId = 1L,
                    startTime = LocalDateTime.of(tomorrow, java.time.LocalTime.of(10, 0)),
                    endTime   = LocalDateTime.of(tomorrow, java.time.LocalTime.of(11, 0)),
                    status    = ReservationStatus.ACTIVE
                )
            )
        )
        // Act
        val slots = viewModel.getReservationTimeSlots(chargerId = 1L)
        val overlapping = slots.find { it.timeLabel == "10:00 - 11:00" && it.date == tomorrow }
        // Assert
        assertNotNull(overlapping)
        assertFalse(overlapping!!.isAvailable)
    }

    @Test   // TC-04b: çakışmayan slot → available
    fun getReservationTimeSlots_slotAvailable_whenNoOverlap() {
        // Arrange – aynı rezervasyon, farklı slot sorgulanıyor
        viewModel.setTestState(
            allReservations = listOf(
                TestFixtures.reservation(
                    chargerId = 1L,
                    startTime = LocalDateTime.of(tomorrow, java.time.LocalTime.of(10, 0)),
                    endTime   = LocalDateTime.of(tomorrow, java.time.LocalTime.of(11, 0)),
                    status    = ReservationStatus.ACTIVE
                )
            )
        )
        // Act
        val slots = viewModel.getReservationTimeSlots(chargerId = 1L)
        val freeSlot = slots.find { it.timeLabel == "12:00 - 13:00" && it.date == tomorrow }
        // Assert
        assertNotNull(freeSlot)
        assertTrue(freeSlot!!.isAvailable)
    }
}