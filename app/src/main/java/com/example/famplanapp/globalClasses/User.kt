package com.example.famplanapp.globalClasses

import com.example.famplanapp.tasks.Task

data class User(
    var id: Int,
    var name: String = "",
    var preferredName: String = "",
    var email: String = "",
    var tasksId: MutableList<String> = mutableListOf(),
    var colour: String = "",
    var role: String = "",
    var settingId: String = "") {

}