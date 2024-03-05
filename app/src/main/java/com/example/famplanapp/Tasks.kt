package com.example.famplanapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.time.LocalDateTime
import java.util.Calendar


sealed class Routes(val route: String) {
    object NewTask : Routes("TaskCreation")
}

@Composable
fun Navigation(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.NewTask.route) {
        composable(Routes.NewTask.route) {
            TasksCreation(navController = navController)
        }
    }
}
@Composable
fun Tasks(navController: NavController) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("TEST ")
        Button(
            onClick = {
                navController.navigate(Routes.NewTask.route)
            },
            modifier = Modifier.align(Alignment.End),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.White,
                contentColor = Color(darkPurple)
            )
        ) {
            Text(
                "+",
                style = TextStyle(
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
