package com.example.famplanapp.classes

import com.example.famplanapp.PollOption
import java.time.LocalDateTime

data class Poll(
    val id: Int,
    val owner: User, // should be User object eventually
    val subject: String,
    val options: List<PollOption>,
    val deadline: LocalDateTime
)