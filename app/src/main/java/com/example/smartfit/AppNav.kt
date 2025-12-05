package com.example.smartfit

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.smartfit.screens.HomeScreen
import com.example.smartfit.screens.LoginScreen
import com.example.smartfit.screens.SettingScreen
import com.example.smartfit.screens.SplashScreen
import com.example.smartfit.viewmodel.ThemeViewModel

@Composable
fun AppNav(themeViewModel: ThemeViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.splash) {
        composable(Routes.splash) {
            SplashScreen(navController)
        }
        composable(Routes.home) {
            HomeScreen(modifier = Modifier, navController)
        }
        composable(Routes.log) {
            // LogActivity(navController) // Uncomment when created
        }
        composable(Routes.setting) {
            // Pass the ViewModel here
            SettingScreen(modifier = Modifier, navController, themeViewModel)
        }
        composable(Routes.profile) {
            // ProfileScreen(navController) // Uncomment when created
        }
        composable(Routes.login) {
            LoginScreen(modifier = Modifier, navController)
        }
    }
}