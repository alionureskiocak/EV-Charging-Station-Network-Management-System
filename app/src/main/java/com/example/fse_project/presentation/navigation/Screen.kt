package com.example.fse_project.presentation.navigation

sealed class Screen(val route : String){

    object LoginScreen : Screen("login_screen")
    object MainScreen : Screen("main_screen/{userId}"){
        fun passId(userId : Long) = "main_screen/$userId"
    }
}