package com.example.fse_project.unit_tests

import com.example.fse_project.TestFixtures
import com.example.fse_project.data.local.database.entities.ConnectorType
import com.example.fse_project.presentation.home.MainViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class MainViewModelTest {

    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        viewModel = TestFixtures.buildViewModel()
    }

    @Test
    fun canUserMakeReservation_false_whenConnectorMismatch() {
        viewModel.setTestState(
            vehicle = TestFixtures.vehicle(ConnectorType.CCS),
            charger = TestFixtures.charger(ConnectorType.CHADEMO)
        )
        val result = viewModel.canUserMakeReservation()
        assertFalse(result)
    }

    @Test
    fun canUserMakeReservation_true_whenConnectorMatch() {
        // Arrange
        viewModel.setTestState(
            vehicle = TestFixtures.vehicle(ConnectorType.CCS),
            charger = TestFixtures.charger(ConnectorType.CCS)
        )
        val result = viewModel.canUserMakeReservation()
        assertTrue(result)
    }
}