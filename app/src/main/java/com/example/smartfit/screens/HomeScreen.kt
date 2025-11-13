package com.example.smartfit.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.smartfit.NavItem
import com.example.smartfit.ui.theme.Background


@Composable
fun HomeScreen(modifier: Modifier = Modifier,navController: NavController) {

    val navList = listOf(
        NavItem("Home", Icons.Default.Home),
        NavItem("Activity", Icons.AutoMirrored.Filled.DirectionsWalk),
        NavItem("Profile", Icons.Default.Person),
    )

    var selectedIndex by remember{
        mutableStateOf(0)
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = Background
            ) {

                navList.forEachIndexed { index, navItem ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = {
                            selectedIndex = index
                        },
                        icon = {
                            Icon(imageVector = navItem.icon, contentDescription = "Icon")
                        },
                        label = {
                            Text(text = navItem.label)
                        }
                    )
                }
            }
        }
    ) { innerpadding ->
        ScreenContent(modifier = Modifier.padding(innerpadding),selectedIndex,navController)

    }
}

@Composable
fun ScreenContent(modifier: Modifier = Modifier, selectedIndex: Int, navController: NavController){
    when(selectedIndex){
        0->HomeContent(navController = navController)
        1->LogActivity()
        2->ProfileScreen()
    }

}
