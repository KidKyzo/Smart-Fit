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
import com.example.smartfit.data.repository.ActivityRepository
import com.example.smartfit.data.repository.UserRepository
import com.example.smartfit.screens.SplashScreen
import com.example.smartfit.screens.activity.LogActivity
import com.example.smartfit.screens.auth.BioDataScreen
import com.example.smartfit.screens.auth.LoginScreen
import com.example.smartfit.screens.auth.RegisterScreen
import com.example.smartfit.screens.auth.RegisterScreen
import com.example.smartfit.screens.auth.BioDataScreen
import com.example.smartfit.screens.home.HomeContent
import com.example.smartfit.screens.profile.ProfileScreen
import com.example.smartfit.screens.profile.SettingScreen
import com.example.smartfit.viewmodel.ActivityViewModel
import com.example.smartfit.viewmodel.ActivityViewModelFactory
import com.example.smartfit.viewmodel.ThemeViewModel
import com.example.smartfit.viewmodel.UserViewModel
import com.example.smartfit.viewmodel.UserViewModelFactory
import com.example.smartfit.viewmodel.FoodViewModel
import com.example.smartfit.viewmodel.FoodViewModelFactory

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
    val foodViewModel: FoodViewModel = viewModel(
        factory = FoodViewModelFactory(application)
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

    // Check if we should show bottom navigation
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in listOf(Routes.home, Routes.plan, Routes.profile)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // AnimatedVisibility at AppNav level for proper sync
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(
                    initialOffsetY = { it }, // Slide from bottom
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                ),
                exit = slideOutVertically(
                    targetOffsetY = { it }, // Slide to bottom
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                )
            ) {
                BottomNavigationBar(
                    navController = navController,
                    currentRoute = currentRoute
                )
            }
        }
    ) { innerPadding ->
        // NavHost is the container for all navigation destinations.
        NavHost(
            navController = navController,
            startDestination = Routes.splash,
            modifier = Modifier.padding(innerPadding)
        ) {
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
            composable(Routes.register) {
                RegisterScreen(navController = navController)
            }
            composable(Routes.biodata) {
                BioDataScreen(
                    navController = navController,
                    userViewModel = userViewModel
                )
            }
            
            // Main bottom navigation screens (top-level routes)
            composable(Routes.home) {
                HomeContent(
                    modifier = Modifier,
                    navController = navController,
                    activityViewModel = activityViewModel,
                    userViewModel = userViewModel,
                    foodViewModel = foodViewModel
                )
            }
            composable(Routes.plan) {
                com.example.smartfit.screens.plan.PlanScreen(
                    navController = navController,
                    viewModel = activityViewModel,
                    foodViewModel = foodViewModel
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
            
            // Other screens
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
            composable(Routes.workoutList) {
                com.example.smartfit.screens.plan.WorkoutListScreen(
                    navController = navController
                )
            }
            composable(Routes.foodList) {
                com.example.smartfit.screens.plan.FoodListScreen(
                    navController = navController,
                    foodViewModel = foodViewModel
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
                    foodId = foodId,
                    foodViewModel = foodViewModel
                )
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
                    weekOffset = weekOffset
                )
            }
        }
    }
}

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
                            // Pop up to the start destination and save state
                            popUpTo(Routes.home) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = "${item.label} navigation"
                    )
                },
                label = {
                    Text(text = item.label)
                },
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
