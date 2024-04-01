package com.example.famplanapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.util.Calendar
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.famplanapp.schedule.eventList
import com.example.famplanapp.tasks.TasksViewModel
import com.example.famplanapp.voting.pollList

//val CustomPurple = Color(0xFF5D3FD3)
val taskSentences = listOf(
    "Unload the dishwasher",
    "Clean kitchen",
    "Buy groceries",
)
val pollsSentences = listOf(
    "What do you want for dinner?",
    "Another poll",
    "Another poll",
)
val eventsSentences = listOf(
    "Soccer game",
    "Family dinner",
    "Sunday brunch",
)
@Composable
fun Home(innerPadding: PaddingValues, viewModel: TasksViewModel, navController: NavController) {
    val currentTime = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val tasksState = viewModel.tasksList.observeAsState(initial = emptyList())
    val taskTitles = tasksState.value.map { it.title }

    val pollTitles = pollList.map { it.subject }
    val eventTitles = eventList.map { it.title }

    // Determine the appropriate greeting based on the current time
    val greeting = when (currentTime) {
        in 0..11 -> "Good Morning"      // If time is between 0 and 11 (inclusive), it's morning
        in 12..16 -> "Good Afternoon"   // If time is between 12 and 16 (inclusive), it's afternoon
        else -> "Good Night" // Otherwise, it's night

    }
    val name = currUser.preferredName


    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "$greeting $name!",
            style = MaterialTheme.typography.h5,
            modifier = Modifier.padding(16.dp, 54.dp, 0.dp, 4.dp)
        )

        //Section(title = "Upcoming Tasks:") {
            // Content for Upcoming T section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 20.dp, 20.dp, 0.dp),
                elevation = 4.dp,
                backgroundColor = Color(lightPurple),
                shape = RoundedCornerShape(16.dp)
            ) {

                Column(Modifier.background(Color(lightPurple)).padding(10.dp)) {
                    Text("Upcoming Tasks",
                        modifier = Modifier.padding(10.dp),
                        fontSize = 16.sp)
                    LazyButtons(
                        sentences = taskTitles,
                        navController = navController,
                        "Tasks"
                    )
                }
            }
        //}

        //Section(title = "Upcoming Polls:") {
            // Content for Upcoming P section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 20.dp, 20.dp, 0.dp),
                elevation = 4.dp,
                backgroundColor = Color(lightPurple),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.background(Color(lightPurple)).padding(10.dp)) {
                    Text("Upcoming Polls",
                        modifier = Modifier.padding(10.dp),
                        fontSize = 16.sp)
                    LazyButtons(sentences = pollTitles,navController = navController,"Voting" )
                }
            }
        //}

        //Section(title = "Upcoming Events") {
            // Content for Upcoming B section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 20.dp, 20.dp, 0.dp),
                elevation = 4.dp,
                backgroundColor = Color(lightPurple),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.background(Color(lightPurple)).padding(10.dp)) {
                    Text("Upcoming Events",
                        modifier = Modifier.padding(10.dp),
                        fontSize = 16.sp)
                    LazyButtons(sentences = eventTitles, navController = navController, "Schedule")
                }
            }
        //}
    }
}

@Composable
fun Section(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(15.dp, 15.dp, 0.dp, 15.dp)
        )
        content()
    }
}


@Composable
fun LazyButtons(sentences: List<String>, navController: NavController,routeName: String) {
    LazyColumn(modifier = Modifier
        .heightIn(0.dp, 130.dp)
        .padding(start = 10.dp)) {
        items(sentences.size) { index ->
            Button(
                onClick = {
                    navController.navigate(routeName){
                        popUpTo(navController.graph.startDestinationRoute!!) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                modifier = Modifier
                    .width(320.dp) // Set the width of the button
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp), // Set the corners to curve with a radius of 10dp
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.White,
                    contentColor = Color(darkPurple)
                )
            ) {
                Text(sentences[index], style = TextStyle(
                        fontWeight = FontWeight.Bold,
                    fontSize = if (sentences[index].length > 1) 15.sp else 16.sp
                )

                )
            }
            Spacer(modifier = Modifier.height(15.dp))
        }
    }
}


