package com.example.smartfit.screens

import android.R
import android.provider.CalendarContract
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CheckboxDefaults.colors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.smartfit.Routes
import com.example.smartfit.ui.theme.Primary
import com.example.smartfit.ui.theme.TextBlack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(modifier: Modifier = Modifier, navController: NavController){
    TopAppBar(
        title = {
            Text(
                text = "Welcome to SmartFit",
                fontWeight = FontWeight.Bold
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Primary,
            titleContentColor = TextBlack
        ),
        actions = {
            IconButton(onClick = {
                navController.navigate(Routes.setting)
            }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Profile",
                    tint = TextBlack
                )
            }
        }
    )
    Column (
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = "Home Content",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = TextBlack
        )

    }
}
