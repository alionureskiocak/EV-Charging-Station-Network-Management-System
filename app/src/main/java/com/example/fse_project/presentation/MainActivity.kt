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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.fse_project.presentation.navigation.Screen
import com.example.fse_project.presentation.login.LoginScreen
import com.example.fse_project.presentation.home.MainScreen
import com.example.fse_project.presentation.home.MainViewModel
import com.example.fse_project.presentation.navigation.BottomNavigationBarItem
import com.example.fse_project.presentation.profile.ProfileScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            var selectedItemIndex by remember { mutableIntStateOf(0) }
            val items = listOf<BottomNavigationBarItem>(
                BottomNavigationBarItem(
                    title = "main",
                    selectedIcon = Icons.Filled.Home,
                    unselectedIcon = Icons.Outlined.Home
                ), BottomNavigationBarItem(
                    title = "profile",
                    selectedIcon = Icons.Filled.Person,
                    unselectedIcon = Icons.Outlined.Person
                )
            )
            val navController = rememberNavController()
            Scaffold(
                bottomBar = {
                    NavigationBar {
                        items.forEachIndexed { index, item ->
                            NavigationBarItem(
                                selected = selectedItemIndex == index,
                                onClick = {
                                    selectedItemIndex = index
                                    when (index) {
                                        0 -> navController.navigate(Screen.MainScreen.route)
                                        1 -> navController.navigate(Screen.ProfileScreen.route)
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (selectedItemIndex == index) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.title
                                    )
                                }
                            )
                        }
                    }
                }
            ) { padding ->
                /* NavHost(navController = navController, startDestination = Screen.LoginScreen.route,
                    modifier = Modifier.padding(padding)
                    ){
                    composable(Screen.LoginScreen.route) {
                        LoginScreen(navController)
                    }
                    composable(Screen.MainScreen.route)  {
                        MainScreen(navController = navController)
                    }
                    composable(Screen.ProfileScreen.route) {
                        ProfileScreen()
                    }
                }
            }*/

                NavHost(
                    navController = navController,
                    startDestination = "auth"
                ) {

                    navigation(
                        startDestination = Screen.LoginScreen.route,
                        route = "auth"
                    ) {
                        composable(Screen.LoginScreen.route) {
                            LoginScreen(navController)
                        }
                    }

                    navigation(
                        startDestination = Screen.MainScreen.route,
                        route = "main_graph"
                    ) {

                        composable(Screen.MainScreen.route) { backStackEntry ->

                            val parentEntry = remember(backStackEntry) {
                                navController.getBackStackEntry("main_graph")
                            }

                            val viewModel: MainViewModel = hiltViewModel(parentEntry)

                            MainScreen(
                                navController = navController,
                                viewModel = viewModel
                            )
                        }

                        composable(Screen.ProfileScreen.route) { backStackEntry ->

                            val parentEntry = remember(backStackEntry) {
                                navController.getBackStackEntry("main_graph")
                            }

                            val viewModel: MainViewModel = hiltViewModel(parentEntry)

                            ProfileScreen(viewModel)
                        }
                    }
                }


            }
        }
    }
}
