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
import androidx.compose.runtime.livedata.observeAsState
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
import com.example.famplanapp.currUser
import com.example.famplanapp.darkPurple
import com.example.famplanapp.lightPurple
import com.example.famplanapp.tasks.TasksDatePicker
import com.example.famplanapp.tasks.TasksTimePicker
import com.example.famplanapp.tasks.localDateTimeToTimestamp
import com.google.firebase.Timestamp
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId

@Composable
fun Voting(votingViewModel: VotingViewModel, innerPadding: PaddingValues) {
    var showPollCreationDialog by remember { mutableStateOf(false) }

    // Screen content for Voting
    Box(modifier = Modifier.fillMaxSize()) {

        PollList(votingViewModel)

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
                    votingViewModel.addPoll(poll)
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
fun PollCard(votingViewModel: VotingViewModel, poll: Poll) {

    // Calculate the time left until the deadline
    val deadlineLocalDateTime = poll.deadline?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDateTime()

    val nowLocalDateTime = LocalDateTime.now(ZoneId.systemDefault())
    val timeLeft: Duration = if (deadlineLocalDateTime != null) {
        Duration.between(nowLocalDateTime, deadlineLocalDateTime)
    } else {
        Duration.ofSeconds(-1)
    }

    // colors for active vs inactive polls
    val bColor = if (timeLeft.isNegative) Color.LightGray else Color(lightPurple)
    val cColor = if (timeLeft.isNegative) Color.DarkGray else Color(darkPurple)

    // votes
    var votedOption by remember { mutableStateOf<String?>(null) }
    val maxVotes = if (timeLeft.isNegative) poll.options.maxOf { it.votes } else 0


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp, 20.dp, 20.dp, 0.dp),
        elevation = 4.dp,
        backgroundColor = bColor,
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
                    text = if (poll.owner == currUser) "From me:" else "From ${poll.owner.name}:",
                    style = MaterialTheme.typography.subtitle2,
                    color = cColor
                )

                // time left to vote
                Text(
                    text = if (timeLeft.isNegative) "Poll ended" else "${
                       timeLeft.toHours()
                    }h left",
                    style = MaterialTheme.typography.subtitle2,
                    color = cColor,
                    textAlign = TextAlign.End
                )
            }

            // poll title
            Text(
                text = poll.subject,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = cColor,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
            )

            // poll option buttons
            poll.options.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    if (timeLeft.isNegative) {
                        val isWinningOption = timeLeft.isNegative && option.votes == maxVotes
                        val buttonBackgroundColor = if (isWinningOption) Color(0xFFC9BFD6) else Color(0xFFEBEAE8)

                        Button(
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(
                                disabledBackgroundColor = buttonBackgroundColor,
                            ),
                            shape = RoundedCornerShape(8.dp),
                            enabled = false,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        ) {
                            Text(
                                text = option.option,
                                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                                color = cColor
                            )
                        }

                        // Display vote count for inactive polls
                        Text(
                            "${option.votes}",
                            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                            color = cColor
                        )
                    } else {
                        val isSelected = option.option == votedOption
                        val buttonBackgroundColor = if (isSelected) Color.LightGray else Color.White
                        Button(
                            onClick = {
                                if (!timeLeft.isNegative && votedOption == null) {
                                    votedOption = option.option
                                    votingViewModel.voteOption(option.option, option.votes)
                                }
                            },
                            enabled = !timeLeft.isNegative,
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = buttonBackgroundColor,
                                contentColor = cColor
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = option.option,
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
    }
}

@Composable
fun PollList(votingViewModel: VotingViewModel) {
    val polls = votingViewModel.pollsList.observeAsState(initial = emptyList()).value

    val backgroundColor = Color(darkPurple)
    Surface(color = backgroundColor, modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.padding(bottom = 70.dp, top=50.dp)) {
            items(polls) { poll ->
                PollCard(votingViewModel, poll)
            }
        }
    }
}


@Composable
fun PollCreationScreen(onPollCreated: (Poll) -> Unit) {
    var title by remember { mutableStateOf("") }
    var options by remember { mutableStateOf(listOf("", "")) }
    var deadline: Timestamp? = null

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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

        Row(
        ) {
            val dueDate = TasksDatePicker("Due Date", null)
            Spacer(modifier = Modifier.width(8.dp))
            val dueTime = TasksTimePicker()
            deadline = localDateTimeToTimestamp(dueDate?.withHour(dueTime.first)?.withMinute(dueTime.second))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (title.isNotBlank() && options.all { it.isNotBlank() }) {
                    onPollCreated(
                        Poll(
                            id = "",
                            owner = currUser,
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
