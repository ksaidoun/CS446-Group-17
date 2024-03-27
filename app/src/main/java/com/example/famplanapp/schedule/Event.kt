package com.example.famplanapp.schedule

import com.example.famplanapp.globalClasses.User
import java.util.Date

class Event(var id: Int = 0, var title: String = "", var date: Date = Date(), var invitedUsers: List<User>) {

}