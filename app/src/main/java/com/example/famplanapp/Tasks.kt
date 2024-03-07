package com.example.famplanapp

import androidx.compose.foundation.background
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
import com.example.famplanapp.classes.Task
import java.time.format.DateTimeFormatter

var tasksList = mutableListOf<Task>()

@Composable
fun TaskDisplayArea(tasks: List<Task>, deleteTask: (Task) -> Unit) {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(tasks){task ->
            TaskItem(task, deleteTask = { deleteTask(task) })
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun TaskItem(task: Task, deleteTask: () -> Unit) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val formattedDateTime = task.dueDate?.format(formatter)

    Card(
        backgroundColor = Color(lightPurple),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = task.title,
                style = TextStyle(fontSize = 20.sp)
            )
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
