package com.example.smartfit

import android.app.Application
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.smartfit.data.database.AppDatabase
import com.example.smartfit.data.network.NetworkModule
import com.example.smartfit.data.repository.ActivityRepository
import com.example.smartfit.data.repository.UserRepository
import com.example.smartfit.data.repository.ExerciseRepository
import com.example.smartfit.screens.SplashScreen
import com.example.smartfit.screens.activity.LogActivity
import com.example.smartfit.screens.auth.BioDataScreen
import com.example.smartfit.screens.auth.LoginScreen
import com.example.smartfit.screens.auth.RegisterScreen
import com.example.smartfit.screens.home.HomeContent
import com.example.smartfit.screens.profile.ProfileScreen
import com.example.smartfit.screens.profile.SettingScreen
import com.example.smartfit.viewmodel.ActivityViewModel
import com.example.smartfit.viewmodel.ActivityViewModelFactory
import com.example.smartfit.viewmodel.ExerciseViewModel
import com.example.smartfit.viewmodel.ExerciseViewModelFactory
import com.example.smartfit.viewmodel.FoodViewModel
import com.example.smartfit.viewmodel.FoodViewModelFactory
import com.example.smartfit.viewmodel.ThemeViewModel
import com.example.smartfit.viewmodel.UserViewModel
import com.example.smartfit.viewmodel.UserViewModelFactory


@Composable
fun AppNav(themeViewModel: ThemeViewModel) {
    val navController = rememberNavController()
    val context = navController.context
    val application = context.applicationContext as Application

    // 1. Initialize Database & Repositories
    val database = AppDatabase.getDatabase(context)
    val activityRepository = ActivityRepository(database.activityDao())
    val userRepository = UserRepository(context)

    // --------------------------------------------

    // 2. Initialize ViewModels
    val activityViewModel: ActivityViewModel = viewModel(
        factory = ActivityViewModelFactory(application, activityRepository, userRepository)
    )
    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(userRepository)
    )
    val foodViewModel: FoodViewModel = viewModel(
        factory = FoodViewModelFactory(application)
    )

    // --- NEW: Initialize Workout ViewModel ---
    val exerciseApi = NetworkModule.provideExerciseDbApi()
    val exerciseRepository = ExerciseRepository(exerciseApi)
    val exerciseViewModel: ExerciseViewModel = viewModel(
        factory = ExerciseViewModelFactory(exerciseRepository)
    )

    // -----------------------------------------

    // Observe login state
    val isLoggedIn by userViewModel.isLoggedIn.collectAsState()

    // Automatic Navigation Logic (Auth Guard)
    LaunchedEffect(isLoggedIn, navController) {
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        if (!isLoggedIn && currentRoute != Routes.login && currentRoute != Routes.splash) {
            navController.navigate(Routes.login) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    // Bottom Bar Logic
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in listOf(Routes.home, Routes.plan, Routes.profile)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(300, easing = FastOutSlowInEasing)),
                exit = slideOutVertically(targetOffsetY = { it }, animationSpec = tween(300, easing = FastOutSlowInEasing))
            ) {
                BottomNavigationBar(navController = navController, currentRoute = currentRoute)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.splash,
            modifier = Modifier.padding(innerPadding)
        ) {
            // ... (Other Auth/Home Routes remain the same) ...

            composable(Routes.splash) { SplashScreen(navController, userViewModel) }
            composable(Routes.login) { LoginScreen(Modifier, navController, userViewModel) }
            composable(Routes.register) { RegisterScreen(navController) }
            composable(Routes.biodata) { BioDataScreen(navController, userViewModel) }

            composable(Routes.home) {
                HomeContent(Modifier, navController, activityViewModel, userViewModel, foodViewModel)
            }

            composable(Routes.plan) {
                com.example.smartfit.screens.plan.PlanScreen(
                    navController = navController,
                    activityViewModel = activityViewModel,
                    foodViewModel = foodViewModel,
                    exerciseViewModel = exerciseViewModel
                )
            }

            composable(Routes.profile) {
                ProfileScreen(navController, themeViewModel, userViewModel, activityViewModel)
            }

            // ... (Log, Setting Routes remain the same) ...

            composable(Routes.log) {
                LogActivity(viewModel = activityViewModel, onBack = { navController.popBackStack() })
            }
            composable(Routes.setting) {
                SettingScreen(Modifier, navController, themeViewModel, userViewModel)
            }

            // --- UPDATED: WORKOUT LIST ROUTE ---
            composable(Routes.workoutList) {
                com.example.smartfit.screens.plan.WorkoutListScreen(
                    navController = navController,
                    viewModel = exerciseViewModel
                )
            }
            // -----------------------------------

            composable(Routes.foodList) {
                com.example.smartfit.screens.plan.FoodListScreen(navController, foodViewModel)
            }

            composable(Routes.workoutDetail) {
                // Do NOT try to get arguments here. Just render the screen.
                com.example.smartfit.screens.plan.WorkoutDetailScreen(
                    navController = navController,
                    viewModel = exerciseViewModel // Pass the SAME INSTANCE
                )
            }

            composable("${Routes.foodDetail}/{foodId}") { backStackEntry ->
                val foodId = backStackEntry.arguments?.getString("foodId")?.toIntOrNull() ?: 0
                com.example.smartfit.screens.plan.FoodDetailScreen(navController, foodId, foodViewModel)
            }

            composable("calorie_intake_history") {
                com.example.smartfit.screens.plan.CalorieIntakeHistoryScreen(
                    navController = navController,
                    foodViewModel = foodViewModel
                )
            }

            composable("${Routes.weeklyReport}/{weekOffset}") { backStackEntry ->
                val weekOffset = backStackEntry.arguments?.getString("weekOffset")?.toIntOrNull() ?: 0
                com.example.smartfit.screens.profile.WeeklyReportScreen(
                    navController = navController,
                    viewModel = activityViewModel,
                    foodViewModel = foodViewModel,
                    weekOffset = weekOffset
                )
            }
        }
    }
}

// ... BottomNavigationBar remains the same ...
@Composable
fun BottomNavigationBar(
    navController: NavController,
    currentRoute: String?
) {
    val items = listOf(
        BottomNavItem("Plan", Routes.plan, Icons.AutoMirrored.Filled.DirectionsWalk),
        BottomNavItem("Home", Routes.home, Icons.Default.Home),
        BottomNavItem("Profile", Routes.profile, Icons.Default.Person)
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 3.dp
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(Routes.home) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(item.icon, contentDescription = "${item.label} navigation") },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

data class BottomNavItem(
    val label: String,
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)