package com.example.famplanapp.tasks
import com.example.famplanapp.globalClasses.User
import com.google.errorprone.annotations.Keep
import com.google.firebase.Timestamp

data class Task(
    var id: String = "",
    var title: String = "",
    var dueDate: Timestamp? = null,
    var remindTime: Timestamp? = null,
    var assignee: User? = null,
    var notes: String = "",
    var isCompleted: Boolean = false
)