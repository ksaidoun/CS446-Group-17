package com.example.famplanapp.globalClasses

import com.example.famplanapp.tasks.Task

data class User(
    var userId: String = "",
    var familyId: String = "",
    var name: String = "",
    var preferredName: String = "",
    var email: String = "",
    var tasksIds: MutableList<String>,
    var colour: String = "",
    var role: String = "",
    var settingId: String = "")