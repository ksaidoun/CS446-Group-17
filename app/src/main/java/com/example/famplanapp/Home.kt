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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

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
fun Home(innerPadding: PaddingValues) {
    val currentTime = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

    // Determine the appropriate greeting based on the current time
    val greeting = when (currentTime) {
        in 0..11 -> "Good Morning"      // If time is between 0 and 11 (inclusive), it's morning
        in 12..16 -> "Good Afternoon"   // If time is between 12 and 16 (inclusive), it's afternoon
        else -> "Good Night" // Otherwise, it's night

    }
    val name = "Brandon"


    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "$greeting $name!",
            style = MaterialTheme.typography.h5,
            modifier = Modifier.padding(16.dp, 10.dp, 0.dp, 10.dp)
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
                    LazyButtons(sentences = taskSentences)
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
                    LazyButtons(sentences = pollsSentences)
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
                    LazyButtons(sentences = eventsSentences)
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
fun LazyButtons(sentences: List<String>) {
    LazyColumn(modifier = Modifier
        .heightIn(0.dp, 130.dp)
        .padding(start = 10.dp)) {
        items(sentences.size) { index ->
            Button(
                onClick = {},
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
                    fontSize = 18.sp
                )
                )
            }
            Spacer(modifier = Modifier.height(15.dp))
        }
    }
}


