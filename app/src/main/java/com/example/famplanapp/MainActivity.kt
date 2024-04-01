package com.example.famplanapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.example.famplanapp.tasks.TasksViewModel
import com.example.famplanapp.ui.theme.FamPlanAppTheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage


// Define anything to be used across the app here maybe?
val darkPurple = 0xFF220059
val lightPurple = 0xFFEDE7F7
val storage = Firebase.storage
var storageRef = storage.reference
val firestore = Firebase.firestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tasksViewModel = ViewModelProvider(this)[TasksViewModel::class.java]
        tasksViewModel.tasksList.observe(this) {
            // Update UI with the fetched tasks
        }
        setContent {
            FamPlanAppTheme {
                SignInScreen()
            }
        }
    }
}
