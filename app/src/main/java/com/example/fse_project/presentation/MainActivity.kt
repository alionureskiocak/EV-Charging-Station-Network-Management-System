package com.example.fse_project.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fse_project.presentation.navigation.Screen
import com.example.fse_project.presentation.login.LoginScreen
import com.example.fse_project.presentation.home.MainScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = Screen.LoginScreen.route){
                composable(Screen.LoginScreen.route) {
                    LoginScreen(navController)
                }
                composable(Screen.MainScreen.route){
                    MainScreen()
                }
            }


        }
    }
}