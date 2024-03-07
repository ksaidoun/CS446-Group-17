package com.example.famplanapp.classes

class User(var name: String = "", var preferredName: String = "", var email: String = "", var tasks: List<Task> = mutableListOf<Task>(), var colour: String = "", var role: String = "",settings: AppSettings = AppSettings()) {

}