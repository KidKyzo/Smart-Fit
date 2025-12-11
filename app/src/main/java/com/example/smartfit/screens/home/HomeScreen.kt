package com.example.smartfit.screens.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Stairs
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.smartfit.NavItem
import com.example.smartfit.screens.activity.LogActivity
import com.example.smartfit.screens.profile.ProfileScreen
import com.example.smartfit.viewmodel.ActivityViewModel
import com.example.smartfit.viewmodel.ThemeViewModel
import com.example.smartfit.viewmodel.UserViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    activityViewModel: ActivityViewModel,
    themeViewModel: ThemeViewModel,
    userViewModel: UserViewModel
) {
    val navList = listOf(
        NavItem("Plan", Icons.AutoMirrored.Filled.DirectionsWalk),
        NavItem("Home", Icons.Default.Home),
        NavItem("Profile", Icons.Default.Person),
    )

    var selectedIndex by remember{ mutableIntStateOf(1) } // Home is now in the middle

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                navList.forEachIndexed { index, navItem ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = {
                            selectedIndex = index
                        },
                        icon = {
                            Icon(
                                imageVector = navItem.icon,
                                contentDescription = "${navItem.label} navigation"
                            )
                        },
                        label = {
                            Text(text = navItem.label)
                        }
                    )
                }
            }
        }
    ) { innerpadding ->
        ScreenContent(
            modifier = Modifier.padding(innerpadding),
            navController = navController,
            activityViewModel = activityViewModel,
            themeViewModel = themeViewModel,
            userViewModel = userViewModel,
            selectedIndex = selectedIndex,
            onNavigate = { selectedIndex = it }
        )
    }
}

@Composable
fun ScreenContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    activityViewModel: ActivityViewModel,
    themeViewModel: ThemeViewModel,
    userViewModel: UserViewModel,
    selectedIndex: Int,
    onNavigate: (Int) -> Unit
) {
    when(selectedIndex) {
        0 -> com.example.smartfit.screens.plan.PlanScreen(
            navController = navController,
            viewModel = activityViewModel
        )
        1 -> HomeContent(
            modifier = modifier,
            navController = navController,
            activityViewModel = activityViewModel,
            userViewModel = userViewModel
        )
        2 -> ProfileScreen(
            navController = navController,
            themeViewModel = themeViewModel, 
            userViewModel = userViewModel,
            activityViewModel = activityViewModel
        )
    }
}