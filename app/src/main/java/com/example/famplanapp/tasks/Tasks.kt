package com.example.famplanapp.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.famplanapp.darkPurple
import com.example.famplanapp.lightPurple
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


var tasksList = mutableListOf<Task>()

@Composable
fun TaskDisplayArea(tasks: List<Task>, deleteTask: (Task) -> Unit) {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(tasks){task ->
            TodoItemRow(task, deleteTask = { deleteTask(task) })
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
@Composable
fun TodoItemRow(task: Task, deleteTask: () -> Unit) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val formattedDateTime = task.dueDate?.format(formatter)
    val currentDateTime = LocalDateTime.now()
    val textColor = if (task.dueDate != null && task.dueDate!!.isBefore(currentDateTime)) {
                        Color.Red
                    } else {
                        Color.Black
                    }
    val checkedState = remember { mutableStateOf(task.isCompleted) }
    Column(

        modifier = Modifier
            .clickable {

            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Checkbox(
                checked = checkedState.value,
                onCheckedChange = { checkedState.value = it },
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
                    style = TextStyle(fontSize = 16.sp),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Text(
                    text = "Due $formattedDateTime",
                    style = TextStyle(fontSize = 16.sp),
                    color = textColor,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
        Text(
            text = "Assignee: ${task.assignee}",
            style = TextStyle(fontSize = 16.sp),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Text(
            text = task.notes,
            style = TextStyle(fontSize = 16.sp),
            modifier = Modifier.padding(horizontal = 16.dp)
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
fun Tasks(innerPadding: PaddingValues) {
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
