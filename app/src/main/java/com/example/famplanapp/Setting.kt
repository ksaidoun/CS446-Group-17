package com.example.famplanapp

import android.content.Context
import android.text.Layout
import android.widget.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.famplanapp.globalClasses.User
import com.example.famplanapp.ui.theme.Purple40
import androidx.compose.material.Button
import androidx.compose.material.Switch
import androidx.compose.ui.platform.LocalContext
import com.example.famplanapp.globalClasses.AppSettings
import com.example.famplanapp.globalClasses.Family
import com.example.famplanapp.globalClasses.FamilyOfUsers


/*
Plan for settings
- on the first page have "Hello username"
- edit name button
- view family button
- button for shared budget
- button for notifications

 */

@Composable
fun Setting(currentUser: User) {

    val context = LocalContext.current

    val curId = currentUser.userId

    var name by remember { mutableStateOf(currentUser.name) }
    var preferredName by remember { mutableStateOf(currentUser.preferredName) }
    var sharedBudgetEnabled by remember { mutableStateOf(false) }
    var notificationEnabled by remember { mutableStateOf(false) }
    var saveSettings by remember { mutableStateOf(false) }


    var users by remember { mutableStateOf(mutableListOf<User>()) }

    val reference = firestore.collection("users").whereEqualTo("familyId",currentUser.familyId)

    reference.get().addOnSuccessListener { querySnapshot ->
        if (!querySnapshot.isEmpty) {
            val documents = querySnapshot.documents
            documents.forEach { document ->
                val user = User(
                    document.getString("userId") ?: "",
                    document.getString("familyId") ?: "",
                    document.getString("name") ?: "No Name",
                    document.getString("preferredName") ?: "No Preference",
                    document.getString("email") ?: "",
                    mutableListOf(),
                    document.getString("colour") ?: "",
                    document.getString("role") ?: "",
                    document.getString("settingId") ?: ""
                )

                val taskIds = document.get("taskIds") as? MutableList<String>
                if (taskIds != null) {
                    user.tasksIds = taskIds
                }
                if(!users.contains(user)){
                    users.add(user)
                }
            }
        }
    }

    Column {
        Spacer(modifier = Modifier.height(70.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ){
            Text(
                text = "Settings",
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                color = Purple40,
                fontWeight = FontWeight.Bold,
                //modifier = Modifier.padding(32.dp)
            )

        }

        Spacer(modifier = Modifier.height(10.dp))
        Column {
            Text(text = "Name:")
            Spacer(modifier = Modifier.height(5.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { newValue ->
                    name = newValue
                },
                modifier = Modifier.padding(16.dp)
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = "Preferred Name:")
            Spacer(modifier = Modifier.height(5.dp))
            OutlinedTextField(
                value = preferredName,
                onValueChange = { newValue ->
                    preferredName = newValue
                },
                modifier = Modifier.padding(16.dp)
            )
        }


        Spacer(modifier = Modifier.height(10.dp))

        // Shared Budget Text Field and Slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Shared Budget for Family:")
            Spacer(modifier = Modifier.width(16.dp))
            if(currentUser.role == "User") {
                Switch(
                    checked = sharedBudgetEnabled,
                    onCheckedChange = { checked ->
                        if (currentUser.role == "Admin"){
                            sharedBudgetEnabled = checked
                        }else{
                            Toast.makeText(context, "Only Admin can edit", Toast.LENGTH_LONG).show()
                        }
                                      },
                    enabled = false,
                )

            }else{
                Switch(
                    checked = sharedBudgetEnabled,
                    onCheckedChange = { sharedBudgetEnabled = it },
                    enabled = true,
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Notification Text Field and Slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Notification")
            Spacer(modifier = Modifier.width(16.dp))
            if(currentUser.role == "User"){
                Switch(
                    checked = notificationEnabled,
                    onCheckedChange = { notificationEnabled = it },
                    enabled = false,
                )
            }else{
                Switch(
                    checked = notificationEnabled,
                    onCheckedChange = { notificationEnabled = it },
                    enabled = true
                )
            }

        }

        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = {
                if (curId.isNotEmpty()) { // Check if userId is not empty or null
                    updateUser(context, curId, name, preferredName)
                    saveSettings = true
                } else {
                    // Handle the case where userId is null or empty
                    Toast.makeText(context, "User ID is null or empty", Toast.LENGTH_LONG).show()
                }
            }
        ) {
            Text("Save")
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ){
            Text(
                text = "Family Members",
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                color = Purple40,
                fontWeight = FontWeight.Bold,
                //modifier = Modifier.padding(32.dp)
            )

        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {
            users.forEach { user ->
                Row(

                ) {
                    Text("${user.preferredName}")
                    Spacer(modifier = Modifier.height(5.dp))
                }
            }
        }
    }
}

private fun updateUser(context: Context, userId: String, name: String, prefName : String) {

    //works when .document("user2")

    firestore.collection("users").document(userId).update("name",name)
        .addOnSuccessListener {
            Toast.makeText(context,"Data added ",Toast.LENGTH_LONG).show()
        }
        .addOnFailureListener {
            Toast.makeText(context," Data not added ",Toast.LENGTH_LONG).show()
        }

    firestore.collection("users").document(userId).update("preferredName",prefName)
        .addOnSuccessListener {
            Toast.makeText(context,"Data added ",Toast.LENGTH_LONG).show()
        }
        .addOnFailureListener {
            Toast.makeText(context," Data not added ",Toast.LENGTH_LONG).show()
        }
}