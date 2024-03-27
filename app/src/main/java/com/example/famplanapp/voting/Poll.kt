package com.example.famplanapp.voting

import com.example.famplanapp.globalClasses.User
import java.time.LocalDateTime

data class Poll(
    val id: Int,
    val owner: User, // should be User object eventually
    val subject: String,
    val options: List<PollOption>,
    val deadline: LocalDateTime
)