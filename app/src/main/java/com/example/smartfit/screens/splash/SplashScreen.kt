package com.example.smartfit.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.smartfit.Routes
import com.example.smartfit.viewmodel.UserViewModel
import kotlinx.coroutines.delay
import androidx.compose.runtime.collectAsState

@Composable
fun SplashScreen(navController: NavController, userViewModel: UserViewModel){
    val isLoggedIn by userViewModel.isLoggedIn.collectAsState()
    
    LaunchedEffect(isLoggedIn) {
        delay(2000) // Simulate loading time
        val targetRoute = if (isLoggedIn) Routes.home else Routes.login
        navController.navigate(targetRoute){
            popUpTo(Routes.splash) { inclusive = true }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary), // Use Theme Secondary
        contentAlignment = Alignment.Center

    ){
        Column(horizontalAlignment = Alignment.CenterHorizontally){
            Text(
                text = "SmartFit",
                color = MaterialTheme.colorScheme.onSecondary, // Readable on Secondary
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Track your Day",
                color = MaterialTheme.colorScheme.onSecondary,
                fontSize = 20.sp
            )
        }
    }
}