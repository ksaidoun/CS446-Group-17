package com.example.famplanapp.voting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.famplanapp.darkPurple
import com.example.famplanapp.globalClasses.User
import com.example.famplanapp.lightPurple
import com.example.famplanapp.tasks.TasksDatePicker
import com.example.famplanapp.tasks.TasksTimePicker
import java.time.Duration
import java.time.LocalDateTime

var pollList = mutableListOf(
    Poll(1, User("Julia"),"What should we have for dinner tonight?",
        listOf(
            PollOption("Chili with rice"),
            PollOption("Chicken stir fry"),
            PollOption("Something else"),
            PollOption("Another option")),
        LocalDateTime.now().plusDays(1)), // Poll ends in 1 day
    Poll(2,  User("Michael"),"How should we spend Family Day 2024?",
        listOf(
            PollOption("Go skiing"),
            PollOption("Go to Niagara Falls"),
            PollOption("Watch a movie")
        ),
        LocalDateTime.now().plusHours(12)), // Poll ends in 12 hours
    Poll(2,  User("Dad"),"Poll question?",
        listOf(PollOption("Option A"), PollOption("Option B"),
            PollOption("Option C"), PollOption("Option D"), PollOption("Option E")),
        LocalDateTime.now().plusHours(12)) // Poll ends in 12 hours,
)

fun addToPolls(pollList: MutableList<Poll>, poll: Poll) {
    pollList.add(poll)
    /* TODO: Sort polls by deadline - soonest first */
}

@Composable
fun Voting(innerPadding: PaddingValues) {
    var showPollCreationDialog by remember { mutableStateOf(false) }

    // Screen content for Voting
    Box(modifier = Modifier.fillMaxSize()) {

        PollList(pollList)

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
        Dialog(onDismissRequest = { showPollCreationDialog = false }) {
            Box(
                modifier = Modifier
                    .background(Color.White)
            ) {
                PollCreationScreen(onPollCreated = { poll ->
                    addToPolls(pollList, poll)
                    showPollCreationDialog = false
                })
                Button(
                    onClick = { showPollCreationDialog = false },
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(bottom = 10.dp, start = 16.dp)
                ) {
                    Text("Close")
                }
            }
        }
    }

}

@Composable
fun PollCard(poll: Poll) {

    // Calculate the time left until the deadline
    val timeLeft = Duration.between(LocalDateTime.now(), poll.deadline)

    // colors for active vs inactive polls
    val backgroundColor = if (timeLeft.isNegative) Color.LightGray else Color(lightPurple)
    val contentColor = if (timeLeft.isNegative) Color.DarkGray else Color(darkPurple)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp, 20.dp, 20.dp, 0.dp),
        elevation = 4.dp,
        backgroundColor = backgroundColor,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // owner of poll
                Text(
                    text = "From ${poll.owner.name}:",
                    style = MaterialTheme.typography.subtitle2,
                    color = contentColor
                )

                // time left to vote
                Text(
                    text = if (timeLeft.isNegative) "Poll ended" else "${Duration.between(LocalDateTime.now(), poll.deadline).toHours()}h left",
                    style = MaterialTheme.typography.subtitle2,
                    color = contentColor,
                    textAlign = TextAlign.End
                )
            }

            // poll title
            Text(text = poll.subject,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = contentColor,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
            )

            // poll option buttons
            poll.options.forEach { option ->
                Button(
                    onClick = { /* TODO: Implement vote action */ },
                    enabled = !timeLeft.isNegative,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.White,
                        contentColor = contentColor,
                        disabledBackgroundColor = Color.White,
                        disabledContentColor = contentColor
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

@Composable
fun PollList(polls: List<Poll>) {
    val backgroundColor = Color(darkPurple)

    Surface(color = backgroundColor, modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.padding(bottom = 70.dp)) {
            items(polls) { poll ->
                PollCard(poll = poll)
            }
        }
    }
}

@Composable
fun PollCreationScreen(onPollCreated: (Poll) -> Unit) {
    var title by remember { mutableStateOf("") }
    var options by remember { mutableStateOf(listOf("", "")) }
    var deadline by remember { mutableStateOf(LocalDateTime.now().plusDays(1)) }

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Create a New Poll", style = MaterialTheme.typography.h6)
        Spacer(modifier = Modifier.height(16.dp))

        // title
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Poll Title") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // options
        options.forEachIndexed { index, option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),

            ) {
                OutlinedTextField(
                    value = option,
                    onValueChange = { newOption ->
                        val mutableOptions = options.toMutableList()
                        mutableOptions[index] = newOption
                        options = mutableOptions
                    },
                    label = { Text("Option ${index + 1}") },
                    modifier = Modifier.width(200.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.White,
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))

                // if more than two options, show 'delete' button
                if (options.size > 2) {
                    IconButton(onClick = {
                        options = options.toMutableList().apply { removeAt(index) }
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Remove option")
                    }
                }
            }
        }

        // if less than 6 options, show "add option" button
        if (options.size < 6) {
            Button(onClick = {
                options = options.toMutableList().apply { add("") }
            }) {
                Text("Add Option")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row (
        ) {
            var dueDate = TasksDatePicker("Due Date", null)
            Spacer(modifier = Modifier.width(8.dp))
            var dueTime = TasksTimePicker()
            deadline = dueDate?.withHour(dueTime.first)?.withMinute(dueTime.second)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (title.isNotBlank() && options.all { it.isNotBlank() }) {
                    onPollCreated(
                        Poll(
                            id = 0,// refactor this when database is implemented
                            owner = User("CurrentUser"), // will get user object eventually
                            subject = title,
                            options = options.map { PollOption(it) },
                            deadline = deadline
                        )
                    )
                }

            },
            modifier = Modifier
                .align(Alignment.End),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.White,
                contentColor = Color(darkPurple)
            )
        ) {
            Text(
                "Create",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            )
        }
    }
}

