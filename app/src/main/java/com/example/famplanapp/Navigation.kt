package com.example.famplanapp


import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.material.*
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun BottomNavBar() {
    val navController = rememberNavController()

    val navItems = listOf(
        NavItem("Home", Icons.Default.Home),
        NavItem("Schedule", Icons.Default.DateRange),
        NavItem("Tasks", Icons.Default.Create),
        NavItem("Voting", Icons.Default.CheckCircle),
        NavItem("Gallery", Icons.Default.AccountBox)
    )
    var selectedItem by remember { mutableIntStateOf(0) }
    Scaffold(
        bottomBar = {
            BottomNavigation {
                navItems.forEachIndexed { index, navItem ->
                    BottomNavigationItem(
                        icon = { Icon(navItem.icon, contentDescription = null) },
                        label = { Text(navItem.title) },
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem=index
                            navController.navigate(navItem.title) {
                                popUpTo(navController.graph.startDestinationRoute!!) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding->
        NavHost(navController, startDestination = "Home") {
            composable("Home") {
                Text("HOME", Modifier.padding(innerPadding))
                // Screen content for Home
            }
            composable("Schedule") {
                // Screen content for Schedule
                Text("Schedule", Modifier.padding(innerPadding))
            }
            composable("Tasks") {
                // Screen content for Tasks
                Text("Tasks", Modifier.padding(innerPadding))
            }
            composable("Voting") {
                // Screen content for Voting
                Text("Voting", Modifier.padding(innerPadding))
            }
            composable("Gallery") {
                // Screen content for Gallery
                Text("Gallery", Modifier.padding(innerPadding))
            }
        }
    }
}

data class NavItem(val title: String, val icon: ImageVector)