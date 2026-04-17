package com.example.fse_project.presentation.home

import androidx.compose.ui.graphics.Color
import com.example.fse_project.domain.model.Charger

data class ChargerItem(
    val charger : Charger,
    val clickable : Boolean,
    val clickableText : String?,
    var statusColor : Color = Color.Gray
){

}
