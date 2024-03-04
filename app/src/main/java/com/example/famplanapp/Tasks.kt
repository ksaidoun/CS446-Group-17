package com.example.famplanapp

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

@Composable
fun TaskCreator(addTask: (Task) -> Unit) {
    var title by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                val task = Task(title = title, notes = notes)
                addTask(task)
                title = ""
                notes = ""
            },

            modifier = Modifier.align(Alignment.End),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White,
                contentColor = Color(darkPurple))
        ) {
            Text("Create Task", style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            )
        }
    }
}

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
    Card(
        backgroundColor = Color(lightPurple),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(task.title)
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
    val tasks = remember { mutableStateListOf<Task>() }

    Column(modifier = Modifier.padding(16.dp)) {
        TaskCreator(addTask = { task -> tasks.add(task)})

        Spacer(modifier = Modifier.height(16.dp))

        TaskDisplayArea(tasks, deleteTask = { task -> tasks.remove(task) })
    }
}

data class Task(val title: String, val notes: String)