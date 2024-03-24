package com.example.famplanapp


import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.famplanapp.gallery.Gallery
import com.example.famplanapp.tasks.Tasks

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BottomNavBar(){
    val photos = mutableListOf<Int>()
    photos.addAll(listOf(
        R.drawable.test1,
        R.drawable.test2,
        R.drawable.test3,
        R.drawable.test4
    ))

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
                            selectedItem = index
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
    ) { innerPadding ->
        NavHost(navController, startDestination = "Home") {
            composable("Home") {
                Home(innerPadding)
                // Screen content for Home
            }
            composable("Schedule") {
                // Screen content for Schedule
                Schedule(innerPadding)
            }
            composable("Tasks") {
                // Screen content for Tasks
                Tasks(innerPadding)
            }
            composable("Voting") {
                // Screen content for Voting
                PollList(samplePosts)
            }
            composable("Gallery") {
                // Screen content for Gallery
                Gallery(photos)
            }
        }
    }
}

data class NavItem(val title: String, val icon: ImageVector)