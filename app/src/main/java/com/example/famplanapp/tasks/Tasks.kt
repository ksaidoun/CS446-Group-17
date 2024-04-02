package com.example.famplanapp.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Dialog
import com.example.famplanapp.currUser
import com.example.famplanapp.firestore
import com.example.famplanapp.getFamilyUsers
import com.example.famplanapp.globalClasses.User
import com.google.firebase.Timestamp
import java.time.format.DateTimeFormatter

var displaying = mutableListOf<String>()

@Composable
fun FilterDropdown(tasksViewModel: TasksViewModel, assignees: MutableList<User?>){
    // expanded/visible dropdown or not
    var expanded by remember { mutableStateOf(false) }
    val filters = listOf("My Tasks", "All Tasks", "Unassigned")
    var selectedFilter by remember { mutableStateOf(tasksViewModel.currFilter) }
    var textFieldSize by remember { mutableStateOf(Size.Zero)}
    // up icon when expanded and down icon when collapsed
    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    Column() {
        OutlinedTextField(
            value = selectedFilter,
            onValueChange = {
                selectedFilter = it
                tasksViewModel.currFilter = selectedFilter },
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .padding(8.dp)
                .onGloballyPositioned { coordinates ->
                    textFieldSize = coordinates.size.toSize()
                },
            label = {Text("Filter")},
            trailingIcon = {
                Icon(icon,"contentDescription",
                    Modifier.clickable { expanded = !expanded })
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current){textFieldSize.width.toDp()})
        ) {
            filters.forEach { label ->
                DropdownMenuItem(onClick = {
                    selectedFilter = label
                    tasksViewModel.currFilter = selectedFilter
                    expanded = false
                }) {
                    Text(text = label)
                }
            }
        }
    }
    TaskDisplayArea(tasksViewModel, assignees)
}

@Composable
fun TaskDisplayArea(tasksViewModel: TasksViewModel, assignees: MutableList<User?>) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedTask by remember { mutableStateOf<Task?>(null) }

    tasksViewModel.setCurrDisplayedTasks()
    displaying.clear()
    if (tasksViewModel.currDisplayedTasks.value?.isNotEmpty() == true) {
        LazyColumn(modifier = Modifier.padding(12.dp)) {
            items(
                items = tasksViewModel.currDisplayedTasks.value!!,
                key = { task -> task.id }
            ) { task ->
                if (task.id !in displaying) {
                    ToDoItem(task) {
                        selectedTask = task
                        showDialog = true
                    }
                    displaying.add(task.id)
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            // Content of the modal dialog
            Box(
                modifier = Modifier
                    .background(Color.White)
                //.padding(16.dp)
            ) {
                TaskEditor(selectedTask!!, tasksViewModel, showDialog, assignees)
                Button(
                    onClick = { showDialog = false },
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

fun isBefore(timestamp1: Timestamp, timestamp2: Timestamp): Boolean {
    if (timestamp1.seconds < timestamp2.seconds) {
        return true
    } else if (timestamp1.seconds == timestamp2.seconds) {
        return timestamp1.nanoseconds < timestamp2.nanoseconds
    }
    return false
}
@Composable
fun ToDoItem(task: Task, onItemClick: (String) -> Unit) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val formattedDateTime = timestampToLocalDateTime(task.dueDate)?.format(formatter)
    val currentDateTime = Timestamp.now()
    val dateColor = if (task.dueDate != null && isBefore(task.dueDate!!, currentDateTime)) {
                        Color.Red
                    } else {
                        Color.Black
                    }
    var textColor = Color.Black
    var textDecor: TextDecoration? = null
    val checkedState = remember { mutableStateOf(task.isCompleted) }
    Column(
        modifier = Modifier.clickable {
            onItemClick(task.id)
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Checkbox(
                checked = checkedState.value,
                onCheckedChange = {
                    checkedState.value = it
                    task.isCompleted = checkedState.value
                },
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = task.title,
                    style = TextStyle(fontSize = 16.sp, textDecoration = textDecor),
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp)
                )
                if (task.dueDate != null) {
                    Text(
                        text = "Due $formattedDateTime",
                        style = TextStyle(fontSize = 16.sp),
                        color = dateColor,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                } else {
                    Text(
                        text = "No due date",
                        style = TextStyle(fontSize = 16.sp),
                        color = Color.Black,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

            }
        }
        Text(
            text = "Assignee: ${task.assignee?.preferredName}",
            style = TextStyle(fontSize = 16.sp),
            modifier = Modifier.padding(start = 16.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = task.notes,
            style = TextStyle(fontSize = 16.sp),
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
fun Tasks(tasksViewModel: TasksViewModel, innerPadding: PaddingValues) {
   // tasksViewModel.fetchTasksFromDb()
    var showDialog by remember { mutableStateOf(false) }
    val noneUser: User? = User(name = "None", preferredName = "None")
    var assignees by remember { mutableStateOf(mutableListOf(noneUser)) }
    val reference = firestore.collection("users").whereEqualTo("familyId", currUser.familyId)
    reference.get().addOnSuccessListener { querySnapshot ->
        val users = getFamilyUsers(querySnapshot)
        for (user in users) {
            if(!assignees.contains(user)){
                assignees.add(user)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize())
    {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(50.dp))
            FilterDropdown(tasksViewModel, assignees)
            // Dropdown menu in the top left goes here
            TaskDisplayArea(tasksViewModel, assignees)
        }
        // Button in the bottom right
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 80.dp, end = 16.dp)
                .size(56.dp)
                .background(MaterialTheme.colors.primary, CircleShape)
                .clickable {
                    showDialog = true
                }
        ) {
            Text(
                text = "+",
                style = TextStyle(color = MaterialTheme.colors.background, fontSize = 24.sp),
                modifier = Modifier.align(Alignment.Center)
            )
        }
        if (showDialog) {
            Dialog(onDismissRequest = { showDialog = false }) {
                // Content of the modal dialog
                Box(
                    modifier = Modifier
                        .background(Color.White)
                    //.padding(16.dp)
                ) {
                    TaskCreator(tasksViewModel, showDialog, assignees)
                    Button(
                        onClick = { showDialog = false },
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
}

