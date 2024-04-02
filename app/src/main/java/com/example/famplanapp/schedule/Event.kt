package com.example.famplanapp.schedule

import com.example.famplanapp.globalClasses.User
import java.util.Date

/*data class Event(var id: String = "", var title: String = "", var date: Date = Date(), var invitedUsers: List<User>, var cost:Int = 0){

}*/

data class Event(
    var id: String = "",
    var title: String = "",
    var date: Date = Date(),
    var invitedUsers: List<User> = listOf(),
    var cost: Int = 0
) {
    // No-argument constructor
    constructor() : this("", "", Date(), listOf(), 0)
}