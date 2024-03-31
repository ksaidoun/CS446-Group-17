package com.example.famplanapp.globalClasses

import com.example.famplanapp.tasks.Task

class UserCopy(
    var name: String = "",
    var preferredName: String = "",
    var email: String = "",
    var tasks: MutableList<Task> = mutableListOf<Task>(),
    var colour: String = "",
    var role: String = "",
    settings: AppSettings = AppSettings()) {

}