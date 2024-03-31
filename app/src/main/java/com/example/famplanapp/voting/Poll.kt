package com.example.famplanapp.voting

import com.example.famplanapp.globalClasses.UserCopy
import java.time.LocalDateTime

data class Poll(
    val id: Int,
    val owner: UserCopy,
    val subject: String,
    val options: List<PollOption>,
    val deadline: LocalDateTime? = null,
    //var voters: List<User> = emptyList()
)

data class PollOption(
    val option: String,
    var votes: Int = 0
)