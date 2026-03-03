package com.example.fse_project.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fse_project.data.local.database.entities.ChargerStatus
import com.example.fse_project.data.local.database.entities.ChargerType
import com.example.fse_project.data.local.database.entities.ConnectorType
import com.example.fse_project.data.local.database.entities.PowerOutput
import com.example.fse_project.domain.model.Charger
import com.example.fse_project.domain.model.Station
import com.example.fse_project.domain.repository.StationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repo : StationRepository
) : ViewModel(){


    val stations = repo.getStationDomainModels().stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        emptyList()
    )
}