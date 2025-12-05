package com.example.smartfit.screens

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.smartfit.Routes
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController){
    LaunchedEffect(true) {
        delay(3000)
        navController.navigate(Routes.login){
            popUpTo(Routes.splash) {inclusive = true}
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