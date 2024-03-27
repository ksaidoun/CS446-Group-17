package com.example.famplanapp


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.famplanapp.gallery.Gallery
import com.example.famplanapp.globalClasses.AppSettings
import com.example.famplanapp.globalClasses.Family
import com.example.famplanapp.globalClasses.User
import com.example.famplanapp.schedule.Schedule
import com.example.famplanapp.tasks.Tasks
import com.example.famplanapp.tasks.tasksList
import com.example.famplanapp.voting.PollCreationScreen
import com.example.famplanapp.voting.PollList
import com.example.famplanapp.voting.samplePosts

// TEST VALUES FOR USERS & FAMILY
val tempSettings: AppSettings = AppSettings(false, "Push")
val tempUser: User = User(
    "David Smith",
    "David",
    "testemail@gmail.com",
    tasksList,
    "#dc143c",
    "Admin",
    tempSettings)
var tempUsers: List<User> = listOf(tempUser)
var family: Family = Family(1, tempSettings, tempUsers)

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
                var showPollCreationDialog by remember { mutableStateOf(false) }

                // Screen content for Voting
                Box(modifier = Modifier.fillMaxSize()) {

                    PollList(samplePosts)

                    Box(
                        modifier = Modifier
                            .padding(16.dp, 76.dp)
                            .size(56.dp)
                            .background(MaterialTheme.colors.primary, CircleShape)
                            .clickable { showPollCreationDialog = true }
                            .align(Alignment.BottomEnd)
                    ) {
                        Text(
                            text = "+",
                            style = TextStyle(
                                color = MaterialTheme.colors.background,
                                fontSize = 24.sp
                            ),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
                if (showPollCreationDialog) {
                    AlertDialog(
                        onDismissRequest = { showPollCreationDialog = false },
                        text = {
                            PollCreationScreen(onPollCreated = { poll ->
                                // TODO: Handle the created poll (e.g., add it to your list of polls)
                                showPollCreationDialog = false
                            })
                        },
                        confirmButton = {},
                        dismissButton = {
                            Button(onClick = { showPollCreationDialog = false }) {
                                Text("Cancel")
                            }
                        }
                    )
                }
            }
            composable("Gallery") {
                // Screen content for Gallery
                Gallery(photos)
            }
        }
    }
}

data class NavItem(val title: String, val icon: ImageVector)