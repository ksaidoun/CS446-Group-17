package com.example.famplanapp.tasks
import com.example.famplanapp.globalClasses.User
import java.time.LocalDateTime

data class Task(
    val id: Int,
    var title: String,
    var dueDate: LocalDateTime? = null,
    var remindTime: LocalDateTime? = null,
    var assignee: User? = null,
    var notes: String = "",
    var isCompleted: Boolean = false
)