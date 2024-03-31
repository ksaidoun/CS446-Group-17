package com.example.famplanapp.globalClasses

data class Family(
    var familyId: String,
    var settingsId: String,
    var userIds: MutableList<String>
)

data class FamilyOfUsers(
    var familyId: String,
    var settingsId: AppSettings,
    var userIds: MutableList<User>
)