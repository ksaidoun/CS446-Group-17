package com.example.famplanapp


import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.famplanapp.gallery.Gallery
import com.example.famplanapp.globalClasses.AppSettings
import com.example.famplanapp.globalClasses.Family
import com.example.famplanapp.globalClasses.User
import com.example.famplanapp.schedule.Schedule
import com.example.famplanapp.tasks.Task
import com.example.famplanapp.tasks.Tasks
import com.example.famplanapp.voting.PollCreationScreen
import com.example.famplanapp.voting.PollList
import com.example.famplanapp.tasks.TasksViewModel
import com.example.famplanapp.voting.Voting


// TEST VALUES FOR USERS & FAMILY

val tempSettings: AppSettings = AppSettings(false, "Push")
val tempUser: User = User(
    "1",
    "",
    "David Smith",
    "David",
    "testemail@gmail.com",
    mutableListOf<String>(),
    "#dc143c",
    "Admin"
)
var tempUsers: List<User> = listOf(tempUser)
var currUser = tempUser


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BottomNavBar(currentUser: User){
    val photos = mutableListOf<Int>()
    photos.addAll(listOf(
        R.drawable.test1,
        R.drawable.test2,
        R.drawable.test3,
        R.drawable.test4
    ))

    val navController = rememberNavController()
    val tasksViewModel: TasksViewModel = viewModel()
    var menuExpanded by remember { mutableStateOf(false) }

    val navItems = listOf(
        //NavItem("Home", Icons.Default.Home),
        NavItem("Schedule", Icons.Default.DateRange),
        NavItem("Tasks", Icons.Default.Create),
        NavItem("Voting", Icons.Default.CheckCircle),
        NavItem("Gallery", Icons.Default.AccountBox)
    )
    var selectedItem by remember { mutableIntStateOf(0) }
    /*
        val viewModel = remember { ExampleViewModel() }
        val scrollState = rememberLazyListState()
        val scrollUpState = viewModel.scrollUp.observeAsState()

        viewModel.updateScrollPosition(scrollState.firstVisibleItemIndex)

        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 56.dp),
                state = scrollState
            ) {
                //content goes here
            }

            ScrollableAppBar(
                title = "ScrollableAppBarExample",
                modifier = Modifier.align(Alignment.CenterStart),
                scrollUpState = scrollUpState
            )
        }
    */
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .height(30.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "FamPlan",
                        )
                    }
                },
                navigationIcon = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (navController != null) {
                            IconButton(
                                onClick = {
                                    navController.navigate("Home") {
                                    popUpTo(navController.graph.startDestinationRoute!!) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                    }
                                },
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.logo),
                                    contentDescription = "Back",
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }
                    }
                },
                actions = {
                    // Show dropdown menu when R.drawable.person icon is clicked
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.person),
                            contentDescription = "Settings Icon",
                            modifier = Modifier.size(30.dp)
                        )
                    }
                },
                /*
                actions = {
                    if (R.drawable.person != null) {
                        IconButton(onClick = {
                            navController.navigate("Setting") {
                                popUpTo(navController.graph.startDestinationRoute!!) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }) {
                            Image(
                                painter = painterResource(id = R.drawable.person),
                                contentDescription = "Settings Icon",
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                },

                 */
                modifier = Modifier.fillMaxWidth()
                        .height(50.dp)
            )
        },
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
                Home(innerPadding, tasksViewModel)
                // Screen content for Home
            }
            composable("Schedule") {
                // Screen content for Schedule
                Schedule(innerPadding)
            }
            composable("Tasks") {
                // Screen content for Tasks
                Tasks(tasksViewModel, innerPadding)
            }
            composable("Voting") {
                Voting(innerPadding)
            }
            composable("Gallery") {
                // Screen content for Gallery
                Gallery(photos)
            }
            composable("Setting") {
                Setting(innerPadding)
                // Screen content for Setting
            }
        }
        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
        ) {
            var members by remember { mutableStateOf<List<String>>(emptyList()) }
            val myfamilyDoc = firestore.collection("families").document(currentUser.familyId).get()
                .addOnSuccessListener { documentSnapshot ->
                    val data = documentSnapshot.data
                    if (data != null) {
                        members = data["userIds"] as List<String>
                    } else {
                        // Handle case where document doesn't exist or doesn't contain expected data
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle failure
                }
            // Populate dropdown menu with family members
            members.forEach { member ->
                DropdownMenuItem(onClick = {
                    menuExpanded = false
                    // Do something when a family member is selected
                }) {
                    Text(member)
                }
            }
        }
    }
}

data class NavItem(val title: String, val icon: ImageVector)

@Composable
fun ScrollableAppBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    background: androidx.compose.ui.graphics.Color = MaterialTheme.colors.primary,
    scrollUpState: State<Boolean?>,
) {
    val position by animateFloatAsState(if (scrollUpState.value == true) -150f else 0f)

    Surface(modifier = Modifier.graphicsLayer { translationY = (position) }, elevation = 8.dp) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(color = background),
        )
        Row(modifier = modifier.padding(start = 12.dp)) {
            if (navigationIcon != null) {
                navigationIcon()
            }
            Image(
                painter = painterResource(id = R.drawable.logowname),
                contentDescription = "Logo",
                modifier = Modifier.size(120.dp)
            )
        }
    }
}

class ExampleViewModel : ViewModel() {
    private var lastScrollIndex = 0

    private val _scrollUp = MutableLiveData(false)
    val scrollUp: LiveData<Boolean>
        get() = _scrollUp

    fun updateScrollPosition(newScrollIndex: Int) {
        if (newScrollIndex == lastScrollIndex) return

        _scrollUp.value = newScrollIndex > lastScrollIndex
        lastScrollIndex = newScrollIndex
    }
}
