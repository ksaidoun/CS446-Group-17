package com.example.famplanapp.voting

import com.example.famplanapp.currUser
import com.example.famplanapp.globalClasses.User
import com.google.firebase.Timestamp
data class Poll(
    var id: String = "",
    val owner: User = currUser,
    val subject: String = "",
    val options: List<PollOption> = emptyList(),
    val deadline: Timestamp? = null,
)

data class PollOption(
    val option: String = "",
    var votes: Int = 0
)