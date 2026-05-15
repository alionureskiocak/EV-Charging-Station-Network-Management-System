package com.example.fse_project

import com.example.fse_project.presentation.home.TimeSlot
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.time.LocalDate

@RunWith(JUnit4::class)
class SlotSelectionTest {

    private lateinit var viewModel: com.example.fse_project.presentation.home.MainViewModel
    private val today = LocalDate.now()

    @Before
    fun setUp() {
        viewModel = TestFixtures.buildViewModel()
        viewModel.setTestState(
            timeSlots = listOf(
                TimeSlot(index = 0, hour = 9,  date = today, timeLabel = "09:00 - 10:00", isAvailable = true),
                TimeSlot(index = 1, hour = 10, date = today, timeLabel = "10:00 - 11:00", isAvailable = true),
                TimeSlot(index = 2, hour = 11, date = today, timeLabel = "11:00 - 12:00", isAvailable = true),
                TimeSlot(index = 3, hour = 12, date = today, timeLabel = "12:00 - 13:00", isAvailable = true),
                TimeSlot(index = 4, hour = 13, date = today, timeLabel = "13:00 - 14:00", isAvailable = true)
            )
        )
    }

    @Test   // TC-03a: 2 slot (2 saat) → izin verilir, seçim korunur
    fun selectTimeSlot_selectionHolds_whenDurationIsExactlyTwoHours() {
        // Act
        viewModel.selectTimeSlot(0)
        viewModel.selectTimeSlot(1)
        // Assert
        assertEquals(0, viewModel.state.value.selectedStartIndex)
        assertEquals(1, viewModel.state.value.selectedEndIndex)
    }

    @Test   // TC-03b: jıgh3 slot (3 saat) → sınır aşıldı, seçim sıfırlanır
    fun selectTimeSlot_resetsSelection_whenDurationExceedsTwoHours() {
        // Act
        viewModel.selectTimeSlot(0) // start: slot 0
        viewModel.selectTimeSlot(1) // end: slot 1 → 2 saat (ok)
        viewModel.selectTimeSlot(2) // slot 2 → 3 saat (aşıldı, sıfırla)
        // Assert: slot 2 yeni start olur
        assertEquals(2, viewModel.state.value.selectedStartIndex)
        assertEquals(2, viewModel.state.value.selectedEndIndex)
    }
}