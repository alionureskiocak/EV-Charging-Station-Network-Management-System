package com.example.fse_project.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.fse_project.presentation.navigation.Screen
import com.example.fse_project.presentation.login.LoginScreen
import com.example.fse_project.presentation.home.MainScreen
import com.example.fse_project.presentation.home.MainViewModel
import com.example.fse_project.presentation.navigation.AppViewModel
import com.example.fse_project.presentation.navigation.AuthState
import com.example.fse_project.presentation.navigation.BottomNavigationBarItem
import com.example.fse_project.presentation.profile.ProfileScreen
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val navController = rememberNavController()
            val appViewModel: AppViewModel = hiltViewModel()
            val authState by appViewModel.authState.collectAsState()

            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination?.route

            // 🔥 EN KRİTİK KISIM (GLOBAL NAVIGATION)
            LaunchedEffect(authState) {

                println("AUTH STATE: $authState")

                when (authState) {

                    is AuthState.Loading -> Unit

                    is AuthState.LoggedOut -> {
                        navController.navigate("auth") {
                            popUpTo(0)
                        }
                    }

                    is AuthState.LoggedIn -> {
                        navController.navigate("main_graph") {
                            popUpTo(0)
                        }
                    }
                }
            }

            Scaffold(
                bottomBar = {
                    if (currentRoute in listOf(
                            Screen.MainScreen.route,
                            Screen.ProfileScreen.route
                        )
                    ) {
                        NavigationBar {

                            NavigationBarItem(
                                selected = currentRoute == Screen.MainScreen.route,
                                onClick = {
                                    navController.navigate(Screen.MainScreen.route) {
                                        popUpTo("main_graph") { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = { Icon(Icons.Default.Home, null) }
                            )

                            NavigationBarItem(
                                selected = currentRoute == Screen.ProfileScreen.route,
                                onClick = {
                                    navController.navigate(Screen.ProfileScreen.route) {
                                        popUpTo("main_graph") { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = { Icon(Icons.Default.Person, null) }
                            )
                        }
                    }
                }
            ) { padding ->

                NavHost(
                    navController = navController,
                    startDestination = "auth",
                    modifier = Modifier.padding(padding)
                ) {

                    // 🔐 AUTH GRAPH
                    navigation(
                        startDestination = Screen.LoginScreen.route,
                        route = "auth"
                    ) {
                        composable(Screen.LoginScreen.route) {
                            LoginScreen(navController)
                        }
                    }

                    // 🏠 MAIN GRAPH
                    navigation(
                        startDestination = Screen.MainScreen.route,
                        route = "main_graph"
                    ) {

                        composable(Screen.MainScreen.route) { backStackEntry ->

                            val parentEntry = remember(backStackEntry) {
                                navController.getBackStackEntry("main_graph")
                            }

                            val viewModel: MainViewModel =
                                hiltViewModel(parentEntry)

                            MainScreen(
                                navController = navController,
                                viewModel = viewModel
                            )
                        }

                        composable(Screen.ProfileScreen.route) { backStackEntry ->

                            val parentEntry = remember(backStackEntry) {
                                navController.getBackStackEntry("main_graph")
                            }

                            val viewModel: MainViewModel =
                                hiltViewModel(parentEntry)

                            ProfileScreen(viewModel)
                        }
                    }
                }
            }
        }
    }
}