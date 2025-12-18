package com.example.smartfit

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartfit.data.datastore.UserPreferences
import com.example.smartfit.ui.theme.SmartFitTheme
import com.example.smartfit.viewmodel.ThemeViewModel
import com.example.smartfit.viewmodel.ThemeViewModelFactory
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Preferences
        val userPreferences = UserPreferences(applicationContext)

        setContent {
            // Initialize ViewModel
            val themeViewModel: ThemeViewModel = viewModel(
                factory = ThemeViewModelFactory(userPreferences)
            )

            // Observe Theme State
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

            // Determine effective theme: Saved Value OR System Default if null
            val effectiveDarkTheme = isDarkTheme ?: isSystemInDarkTheme()

            // Request activity recognition permission for step tracking
            var permissionGranted by remember { mutableStateOf(false) }

            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                permissionGranted = isGranted
            }

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                } else {
                    permissionGranted = true
                }
            }

            SmartFitTheme(darkTheme = effectiveDarkTheme) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    // Pass the ViewModel to AppNav
                    AppNav(themeViewModel = themeViewModel)
                }
            }
        }
    }
}