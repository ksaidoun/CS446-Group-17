package com.example.famplanapp

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.famplanapp.classes.Poll
import com.example.famplanapp.classes.User
import java.time.Duration
import java.time.LocalDateTime



data class PollOption(
    val option: String,
    var votes: Int = 0
)


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PollCard(poll: Poll) {
    // Calculate the time left until the deadline
    val timeLeft = Duration.between(LocalDateTime.now(), poll.deadline)
    val formattedTimeLeft = if (!timeLeft.isNegative) {
        "${timeLeft.toHours()}h left"
    } else {
        "Poll ended"
        /* TODO: Add more logic here (grey out poll card or something) */
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp, 20.dp, 20.dp, 0.dp),
        elevation = 4.dp,
        backgroundColor = Color(lightPurple),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "From ${poll.owner}:",
                    style = MaterialTheme.typography.subtitle2
                )
                Text(
                    text = formattedTimeLeft,
                    style = MaterialTheme.typography.subtitle2,
                    textAlign = TextAlign.End
                )
            }

            Text(text = poll.subject,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = Color(darkPurple),
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
            )

            poll.options.forEach { option ->
                Button(
                    onClick = { /* TODO: Implement vote action */ },

                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.White,
                        contentColor = Color(darkPurple)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp)
                ) {
                    Text(
                        option.option,
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
val samplePosts = listOf(
    Poll(1, User("Julia"),"What should we have for dinner tonight?",
        listOf(PollOption("Chili with rice"), PollOption("Chicken stir fry"),
            PollOption("Something else"), PollOption("Another option")),
        LocalDateTime.now().plusDays(1)), // Poll ends in 1 day
    Poll(2,  User("Michael"),"How should we spend Family Day 2024?",
        listOf(PollOption("Go skiing"), PollOption("Go to Niagara Falls"),
            PollOption("Watch a movie")),
        LocalDateTime.now().plusHours(12)) // Poll ends in 12 hours
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PollList(polls: List<Poll>) {
    val backgroundColor = Color(darkPurple)

    Surface(color = backgroundColor, modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            items(polls) { poll ->
                PollCard(poll = poll)
            }
        }
    }
}
