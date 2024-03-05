package com.example.famplanapp
import java.time.LocalDateTime

data class Task(
    val id: Int,
    var title: String,
    var dueDate: LocalDateTime? = null,
    var remindTime: LocalDateTime? = null,
    var assignee: String = "None",
    var notes: String = ""
)