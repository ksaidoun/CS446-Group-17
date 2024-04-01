package com.example.famplanapp.globalClasses

//Push is the default notification setting. The other option is "Email".
data class AppSettings(
    var settingsId: String = "",
    var sharedBudget: Boolean = false,
    var notificationSettings: String = "Push"
)