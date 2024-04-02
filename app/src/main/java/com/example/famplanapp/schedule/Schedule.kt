package com.example.famplanapp.schedule

import android.content.ContentValues
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.famplanapp.firestore
import com.example.famplanapp.globalClasses.User
import com.example.famplanapp.lightPurple
import com.example.famplanapp.tasks.Task
import com.google.firebase.firestore.toObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Date




var currId = 0
var sharedBudget by mutableStateOf(0)
var users = mutableListOf<User>(User("Bob"), User("Carol"))






fun makeEvent(eventsViewModel: EventsViewModel, eventName: String, attendees: String, cost: String){
    val listNames = attendees.split(", ")
    val listUsers = mutableListOf<User>()
    var index: Int;
    for (name in listNames) {
        index = users.indexOfFirst{it -> it.name == name}
        if(index >= 0) {
            listUsers.add(users[index])
        }
    }
    val myEvent = Event(/*id=currId.toString(), */title=eventName, invitedUsers=listUsers, cost=cost.toInt());
   // ++currId
    //eventList.add(myEvent)
    eventsViewModel.addEvent(myEvent)
    sharedBudget -= cost.toInt()
}

@Composable
fun CalendarUI(eventsViewModel: EventsViewModel,
    currentWeekSunday: LocalDate,
    selectedCells: Set<Pair<Int, Int>>,
    onCellClick: (Int, Int) -> Unit
) {
    //fetchEventsFromDb()
    var isDialogOpen by remember { mutableStateOf(false) }
    var eventName by remember { mutableStateOf(TextFieldValue()) }
    var attendees by remember { mutableStateOf(TextFieldValue()) }
    var cost by remember { mutableStateOf(TextFieldValue()) }
    var selectedDateTime by remember { mutableStateOf(Date()) }

    Column {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .background(Color(lightPurple))
                    .border(0.5.dp, Color.Black)
                ,
                contentAlignment = Alignment.Center
            ){
            }
            repeat(7) { dayIndex ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .background(Color(lightPurple))
                        .border(0.5.dp, Color.Black)
                    ,
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = getDayAbbreviation(dayIndex) + "\n" + (fixRange((currentWeekSunday.dayOfMonth + dayIndex), daysInMonth(currentWeekSunday.monthValue))).toString(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        LazyColumn(Modifier.fillMaxSize().padding(bottom = 50.dp)) {
            items(24) { rowIndex ->
                Row(Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .background(Color(lightPurple))
                            .border(0.5.dp, Color.Black)
                    ){
                        Text(if(rowIndex == 0) "12AM" else (rowIndex % 12).toString() + if(rowIndex < 12) "AM" else "PM")
                    }
                    repeat(7) { columnIndex ->
                        val cellColor = if (selectedCells.contains(rowIndex to columnIndex)) Color.Green else Color.White
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .background(cellColor)
                                .border(0.5.dp, Color.Black)
                                .clickable {
                                    onCellClick(rowIndex, columnIndex)
                                    isDialogOpen = true
                                }
                        ) {}
                    }
                }
            }
        }
        if (isDialogOpen) {
            AlertDialog(
                onDismissRequest = { isDialogOpen = false },
                title = { Text("Event Details") },
                confirmButton = {
                    Button(onClick = {
                        makeEvent(eventsViewModel, eventName.text, attendees.text, cost.text)
                        isDialogOpen = false
                    }) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    Button(onClick = { isDialogOpen = false }) {
                        Text("Cancel")
                    }
                },
                text = {
                    Column {
                        TextField(
                            eventName.text,
                            onValueChange = { eventName = TextFieldValue(it) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.body1,
                            label = { Text("Event Name") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = attendees.text,
                            onValueChange = { attendees = TextFieldValue(it) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.body1,
                            label = { Text("Attendees") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = cost.text,
                            onValueChange = { cost = TextFieldValue(it) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.body1,
                            label = { Text("Cost") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        /*TextField(
                            value = sharedBudget.toString(),
                            onValueChange = { /* Disable editing */ },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.body1,
                            label = { Text("Shared Budget") }
                        )*/
                    }
                },
                properties = DialogProperties(dismissOnClickOutside = false)
            )
        }
    }
}

fun fixRange(num:Int, remainder:Int): Int{
    if(num % remainder == 0){
        return remainder
    }
    return num % remainder
}

fun getDayAbbreviation(dayIndex: Int): String {
    return when (dayIndex) {
        0 -> "Sun"
        1 -> "Mon"
        2 -> "Tue"
        3 -> "Wed"
        4 -> "Thu"
        5 -> "Fri"
        6 -> "Sat"
        else -> throw IllegalArgumentException("Invalid day index: $dayIndex")
    }
}

fun daysInMonth(monthIndex: Int): Int {
    if(monthIndex == 2){
        return 29;
    }
    if(monthIndex <= 7){
        if(monthIndex % 2 == 1){
            return 31
        }
        return 30
    }
    if(monthIndex % 2 == 0){
        return 31
    }
    return 30
}

@Composable
fun Schedule(eventsViewModel: EventsViewModel, innerPadding: PaddingValues) {
    var currentWeekSunday by remember { mutableStateOf(LocalDate.now().with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.SUNDAY))) }
    var selectedCells by remember { mutableStateOf(emptySet<Pair<Int, Int>>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top=50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Week starting from: ${currentWeekSunday.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}",
            style = MaterialTheme.typography.h5
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { currentWeekSunday = currentWeekSunday.plusWeeks(-1) },
            modifier = Modifier.fillMaxWidth()
        ){
            Text("Previous Week", style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            ))
        }
        Button(
            onClick = { currentWeekSunday = currentWeekSunday.plusWeeks(1) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Next Week", style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            ))
        }
        TextField(
            value = sharedBudget.toString(),
            onValueChange = {
                sharedBudget = it.toIntOrNull() ?: 0
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            textStyle = MaterialTheme.typography.body1,
            label = { Text("Shared Budget") }
        )
        CalendarUI(eventsViewModel = eventsViewModel,
            currentWeekSunday = currentWeekSunday,
            selectedCells = selectedCells,
            onCellClick = { rowIndex, columnIndex ->
                selectedCells = setOf(rowIndex to columnIndex)
            }
        )
    }
}
