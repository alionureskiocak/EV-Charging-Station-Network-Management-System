package com.example.fse_project.presentation.navigation

import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavigationBarItem(
    val title : String,
    val selectedIcon : ImageVector,
    val unselectedIcon : ImageVector
)