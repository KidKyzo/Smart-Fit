package com.example.smartfit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.smartfit.data.datastore.UserPreferences
import com.example.smartfit.ui.theme.SmartFitTheme
import com.example.smartfit.viewmodel.ThemeViewModel
import com.example.smartfit.viewmodel.ThemeViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

            SmartFitTheme(darkTheme = effectiveDarkTheme) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    // Pass the ViewModel to AppNav
                    AppNav(themeViewModel = themeViewModel)
                }
            }
        }
    }
}