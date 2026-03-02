package com.example.fse_project.presentation

import androidx.lifecycle.ViewModel
import com.example.fse_project.domain.repository.StationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AsdViewModel @Inject constructor(
    private val repo : StationRepository
) : ViewModel(){



}