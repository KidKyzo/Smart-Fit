package com.example.smartfit

import android.app.Application
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
import com.example.smartfit.screens.SplashScreen
import com.example.smartfit.screens.activity.LogActivity
import com.example.smartfit.screens.auth.LoginScreen
import com.example.smartfit.screens.home.HomeScreen
import com.example.smartfit.screens.profile.ProfileScreen
import com.example.smartfit.screens.profile.SettingScreen
import com.example.smartfit.viewmodel.ActivityViewModel
import com.example.smartfit.viewmodel.ActivityViewModelFactory
import com.example.smartfit.viewmodel.ThemeViewModel
import com.example.smartfit.viewmodel.UserViewModel
import com.example.smartfit.viewmodel.UserViewModelFactory

@Composable
fun AppNav(themeViewModel: ThemeViewModel) {
    val navController = rememberNavController()
    val context = navController.context
    val application = context.applicationContext as Application

    // Initialize database and repositories. These are scoped to the AppNav lifecycle.
    val database = AppDatabase.getDatabase(context)
    val activityRepository = ActivityRepository(database.activityDao())
    val userRepository = UserRepository(context)

    // Initialize ViewModels using their respective factories for dependency injection.
    val activityViewModel: ActivityViewModel = viewModel(
        factory = ActivityViewModelFactory(application, activityRepository, userRepository)
    )
    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(userRepository)
    )

    // Observe the login state from the UserViewModel.
    val isLoggedIn by userViewModel.isLoggedIn.collectAsState()

    // This effect handles automatic navigation based on the user's login status.
    LaunchedEffect(isLoggedIn, navController) {
        val currentRoute = navController.currentBackStackEntry?.destination?.route

        // This effect should ONLY handle forcing a user to the login screen if they are not authorized.
        if (!isLoggedIn && currentRoute != Routes.login && currentRoute != Routes.splash) {
            // If the user is not logged in and not on login/splash, redirect to login.
            navController.navigate(Routes.login) {
                // Clear the entire back stack to prevent going back to a protected screen.
                popUpTo(0) { inclusive = true }
            }
        }
    }

    // NavHost is the container for all navigation destinations.
    NavHost(navController = navController, startDestination = Routes.splash) {
        composable(Routes.splash) {
            SplashScreen(navController, userViewModel)
        }
        composable(Routes.login) {
            LoginScreen(
                modifier = Modifier,
                navController = navController,
                userViewModel = userViewModel
            )
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
            LogActivity(
                viewModel = activityViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.setting) {
            SettingScreen(
                modifier = Modifier,
                navController = navController,
                themeViewModel = themeViewModel
            )
        }
        composable(Routes.profile) {
            ProfileScreen(
                navController = navController,
                themeViewModel = themeViewModel,
                userViewModel = userViewModel,
                activityViewModel = activityViewModel
            )
        }
        composable(Routes.plan) {
            com.example.smartfit.screens.plan.PlanScreen(
                navController = navController,
                viewModel = activityViewModel
            )
        }
        composable(Routes.workoutList) {
            com.example.smartfit.screens.plan.WorkoutListScreen(
                navController = navController
            )
        }
        composable(Routes.foodList) {
            com.example.smartfit.screens.plan.FoodListScreen(
                navController = navController
            )
        }
        composable("${Routes.workoutDetail}/{workoutId}") { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getString("workoutId")?.toIntOrNull() ?: 0
            com.example.smartfit.screens.plan.WorkoutDetailScreen(
                navController = navController,
                workoutId = workoutId
            )
        }
        composable("${Routes.foodDetail}/{foodId}") { backStackEntry ->
            val foodId = backStackEntry.arguments?.getString("foodId")?.toIntOrNull() ?: 0
            com.example.smartfit.screens.plan.FoodDetailScreen(
                navController = navController,
                foodId = foodId
            )
        }
        composable("${Routes.weeklyReport}/{weekOffset}") { backStackEntry ->
            val weekOffset = backStackEntry.arguments?.getString("weekOffset")?.toIntOrNull() ?: 0
            com.example.smartfit.screens.profile.WeeklyReportScreen(
                navController = navController,
                viewModel = activityViewModel,
                weekOffset = weekOffset
            )
        }
    }
}