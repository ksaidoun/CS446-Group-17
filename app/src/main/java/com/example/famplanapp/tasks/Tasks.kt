package com.example.famplanapp.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.famplanapp.darkPurple
import com.example.famplanapp.globalClasses.Family
import com.example.famplanapp.lightPurple
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


var tasksList = mutableListOf<Task>()


@Composable
fun FilterDropdown(){
    // expanded state of the Text Field
    var expanded by remember { mutableStateOf(false) }
    // temporary list of options, eventually use list of users
    val assignees = listOf("My Tasks", "All Tasks", "Unassigned")
    var selectedAssignee by remember { mutableStateOf("My Tasks") }
    var textFieldSize by remember { mutableStateOf(Size.Zero)}
    // Up Icon when expanded and down icon when collapsed
    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    Column() {
        // Create an Outlined Text Field
        // with icon and not expanded
        OutlinedTextField(
            value = selectedAssignee,
            onValueChange = { selectedAssignee = it },
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .padding(8.dp)
                .onGloballyPositioned { coordinates ->
                    // This value is used to assign to
                    // the DropDown the same width
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
            assignees.forEach { label ->
                DropdownMenuItem(onClick = {
                    selectedAssignee = label
                    expanded = false
                }) {
                    Text(text = label)
                }
            }
        }
    }
}
@Composable
fun TaskDisplayArea(tasks: List<Task>, deleteTask: (Task) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedTask by remember { mutableStateOf<Task?>(null) }
    Spacer(modifier = Modifier.height(80.dp))
    FilterDropdown()
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(tasks){task ->
            ToDoItem(task) { clickedIndex ->
                selectedTask = task
                showDialog = true
            }
            Spacer(modifier = Modifier.height(8.dp))
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
                TaskEditor(selectedTask!!, showDialog)
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
@Composable
fun ToDoItem(task: Task, onItemClick: (Int) -> Unit) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val formattedDateTime = task.dueDate?.format(formatter)
    val currentDateTime = LocalDateTime.now()
    val dateColor = if (task.dueDate != null && task.dueDate!!.isBefore(currentDateTime)) {
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
                    /*if (!checkedState.value) {
                        textColor = Color.Gray
                        textDecor = TextDecoration.LineThrough
                    } else {
                        textColor = Color.Black
                        textDecor = null
                    } */
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
                Text(
                    text = "Due $formattedDateTime",
                    style = TextStyle(fontSize = 16.sp),
                    color = dateColor,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
        Text(
            text = "Assignee: ${task.assignee}",
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
fun TaskItem(task: Task, deleteTask: () -> Unit) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val formattedDateTime = task.dueDate?.format(formatter)
    val checkedState = remember { mutableStateOf(task.isCompleted) }
    Card(
        backgroundColor = Color(lightPurple),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(){
                Checkbox(
                    checked = checkedState.value,
                    onCheckedChange = { checkedState.value = it },
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Text(
                    text = task.title,
                    style = TextStyle(fontSize = 20.sp)
                )
            }
            Text(text = "Due: $formattedDateTime")
            Text(text = "Assignee: ${task.assignee}")
            Text(task.notes)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = deleteTask,
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White,
                    contentColor = Color(darkPurple))
                ) {
                Text("Delete", style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ))
            }
        }
    }
}

@Composable
fun Tasks(innerPadding: PaddingValues, family: Family?) {
    val tasks = remember { tasksList }
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Dropdown menu in the top left goes here
            TaskDisplayArea(tasks, deleteTask = { task -> tasks.remove(task) })
        }
        // Button in the bottom right
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 80.dp, end = 16.dp)
        ) {
            OutlinedButton(
                onClick = { showDialog = true }
            ) {
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text("+")
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
                    TaskCreator(addTask = { task -> tasksList.add(task) }, showDialog)
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

