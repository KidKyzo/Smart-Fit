package com.example.smartfit

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.smartfit.data.database.AppDatabase
import com.example.smartfit.data.repository.ActivityRepository
import com.example.smartfit.data.repository.UserRepository
import com.example.smartfit.screens.activity.LogActivity
import com.example.smartfit.screens.auth.LoginScreen
import com.example.smartfit.screens.home.HomeScreen
import com.example.smartfit.screens.profile.ProfileScreen
import com.example.smartfit.screens.profile.SettingScreen
import com.example.smartfit.screens.splash.SplashScreen
import com.example.smartfit.viewmodel.ActivityViewModel
import com.example.smartfit.viewmodel.ActivityViewModelFactory
import com.example.smartfit.viewmodel.ThemeViewModel
import com.example.smartfit.viewmodel.UserViewModel
import com.example.smartfit.viewmodel.UserViewModelFactory

@Composable
fun AppNav(themeViewModel: ThemeViewModel) {
    val navController = rememberNavController()
    val context = navController.context
    
    // Initialize database and repository
    val database = AppDatabase.getDatabase(context)
    val activityRepository = ActivityRepository(database.activityDao())
    val userRepository = UserRepository(context)

    val activityViewModel: ActivityViewModel = viewModel(
        factory = ActivityViewModelFactory(activityRepository, userRepository)
    )
    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(userRepository)
    )

    val isLoggedIn by userViewModel.isLoggedIn.collectAsState()

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn && navController.currentDestination?.route == Routes.login) {
            navController.navigate(Routes.home) {
                popUpTo(Routes.login) { inclusive = true }
            }
        } else if (!isLoggedIn && navController.currentDestination?.route != Routes.login) {
            navController.navigate(Routes.login) {
                popUpTo(Routes.home) { inclusive = true }
            }
        }
    }

    NavHost(navController = navController, startDestination = Routes.splash) {
        composable(Routes.splash) {
            SplashScreen(navController, userViewModel)
        }
        composable(Routes.home) {
            HomeScreen(
                modifier = Modifier,
                navController = navController,
                activityViewModel = activityViewModel,
                themeViewModel = themeViewModel,
                userViewModel = userViewModel
            )
        }
        composable(Routes.log) {
            LogActivity(viewModel = activityViewModel, onBack = { navController.popBackStack() })
        }
        composable(Routes.setting) {
            SettingScreen(modifier = Modifier, navController, themeViewModel)
        }
        composable(Routes.profile) {
            ProfileScreen(
                themeViewModel = themeViewModel,
                userViewModel = userViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.login) {
            LoginScreen(modifier = Modifier, navController, userViewModel)
        }
        composable("activity_detail/{activityId}") { backStackEntry ->
            val activityId = backStackEntry.arguments?.getString("activityId")?.toLongOrNull()
            activityId?.let { id ->
                // ActivityDetailScreen(activityId = id, viewModel = activityViewModel, onBack = { navController.popBackStack() })
            }
        }
    }
}